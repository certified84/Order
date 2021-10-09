package com.certified.order.view

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.bumptech.glide.Glide
import com.certified.order.BuildConfig
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.DialogCardDetailsBinding
import com.certified.order.databinding.FragmentCompleteOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.certified.order.util.Config
import com.certified.order.util.Mailer
import com.certified.order.util.PreferenceKeys
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ConfirmOrderFragment(private val items: List<Item>) : DialogFragment() {

    private lateinit var binding: FragmentCompleteOrderBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteOrderBinding.inflate(layoutInflater)

        auth = Firebase.auth
        PaystackSdk.initialize(requireContext())
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val viewModelFactory = ItemViewModelFactory(items)
        val viewModel: ItemViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(ItemViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recyclerViewItemsToDeliver.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ItemAdapter(items)
        binding.recyclerViewItemsToDeliver.adapter = adapter

        adapter.setOnItemClickedListener(object : ItemAdapter.OnItemClickedListener {
            override fun onItemClick(item: Item) {

            }
        })

        binding.apply {

            val currentUser = auth.currentUser!!
            val db = Firebase.firestore

//            TODO: Obtain the current device location
//            val deliveryAddress = getCurrentLocation()
            val profileImage = currentUser.photoUrl

            tvReceiverPhone.text = preferences.getString(PreferenceKeys.USER_PHONE, "")
            tvAddress.text = preferences.getString(PreferenceKeys.USER_DEFAULT_DELIVERY_ADDRESS, "")
            tvReceiverName.text = currentUser.displayName

            if (profileImage == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(receiverProfileImage)
            else
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)

            tvAddress.setOnClickListener {
                val latitude = getCurrentLocation()?.latitude
                val longitude = getCurrentLocation()?.longitude
//                val position = latitude?.let { it1 -> longitude?.let { it2 -> LatLng(it1, it2) } }
                getCurrentLocation()?.let { it1 -> openMap(it1) }
            }
            tvDeliveryTime.setOnClickListener { openTimePicker() }

            btnCloseDialog.setOnClickListener { dismiss() }
            btnConfirmOrder.setOnClickListener {
                if (tvDeliveryTime.text != "00:00 AM") {
//                    if (tvAddress.text != resources.getString(R.string.click_here_to_set_delivery_address)) {
                    progressBar.visibility = View.VISIBLE

                    val newOrder = Order(
                        currentUser.displayName!!,
                        currentUser.photoUrl,
                        tvReceiverPhone.text.toString(),
                        tvSubtotal.text.toString().toDouble(),
                        items
                    )
                    newOrder.deliveryTime = tvDeliveryTime.text.toString()

                    if (launchPaymentDialog(tvItemTotal.text.toString())) {

//                        TODO: Save the order in general orders for dispatchers to be to access easily

                        val ordersRef = db.collection("orders").document()
                        newOrder.id = ordersRef.id
                        ordersRef.set(newOrder).addOnCompleteListener {
                            if (it.isSuccessful) {

//                        TODO: Save the order in general orders for the users access only

                                val myOrdersRef =
                                    db.collection("orders").document(currentUser.uid)
                                        .collection("my_orders").document()
                                newOrder.id = myOrdersRef.id
                                myOrdersRef.set(newOrder).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

//                                            TODO: Delete all the items in the users cart

                                        val cartRef =
                                            db.collection("cart").document(currentUser.uid)
                                                .collection("my_cart_items")
                                        progressBar.visibility = View.GONE
                                    }
                                }
                                super.dismiss()
                            }
                        }
                    }
//                    } else
//                        Toast.makeText(
//                            requireContext(),
//                            "Please set your delivery address",
//                            Toast.LENGTH_LONG
//                        ).show()
                } else
                    Toast.makeText(
                        requireContext(),
                        "Please set a delivery time",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

    private fun launchPaymentDialog(amount: String): Boolean {
        var success = false
        val cardDetailsDialog =
            DialogCardDetailsBinding.inflate(
                layoutInflater,
                ConstraintLayout(requireContext()),
                false
            )
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        cardDetailsDialog.apply {
            btnConfirmPayment.text = "Pay #$amount"
            btnConfirmPayment.setOnClickListener {
                if (etCardNumber.text.toString().isNotEmpty() && etCardExpiryDate.text.toString()
                        .isNotEmpty() && etCardCvv.text.toString().isNotEmpty()
                ) {
                    val cardNumber = etCardNumber.text.toString().trim()
                    val expiryMonth =
                        etCardExpiryDate.text.toString().substringBefore("/").trim()
                            .toInt()
                    val expiryYear =
                        etCardExpiryDate.text.toString().substringAfter("/").trim()
                            .toInt()
                    val cvv = etCardCvv.text.toString()
                    val card = Card(cardNumber, expiryMonth, expiryYear, cvv)
                    println("expiryMonth: $expiryMonth, expiryYear: $expiryYear")
                    if (card.isValid) {
                        progressBar.visibility = View.VISIBLE
                        val charge = Charge()
                        charge.amount = amount.toInt()
                        charge.email = auth.currentUser?.email
                        charge.card = card

                        success = chargeCard(charge)
                    } else
                        Toast.makeText(
                            requireContext(),
                            "Invalid card",
                            Toast.LENGTH_LONG
                        ).show()
                } else {
                    etCardNumber.error = "*Required"
                    etCardExpiryDate.error = "*Required"
                    etCardCvv.error = "*Required"
                }
            }
            btnCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.setContentView(cardDetailsDialog.root)
        bottomSheetDialog.show()
        return success
    }

    private fun chargeCard(charge: Charge): Boolean {
        var success = false
        PaystackSdk.chargeCard(
            requireActivity(),
            charge,
            object : Paystack.TransactionCallback {
                override fun onSuccess(transaction: Transaction?) {
                    success = true
                    mailAdmin()
                    mailUser()
                }

                override fun beforeValidate(transaction: Transaction?) {
//                    TODO("Not yet implemented")
                }

                override fun onError(
                    error: Throwable?,
                    transaction: Transaction?
                ) {
                    Toast.makeText(
                        requireContext(),
                        "An error occurred: ${error?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        return success
    }

    private fun mailAdmin() =
        binding.apply {
            val email = Config.ADMIN_EMAIL
            val subject = "New Order received"
            val message =
                "A new order has been placed. \nCheck out more information in Firebase console.\n"

            Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    private fun mailUser() =
        binding.apply {
            val email = auth.currentUser!!.email!!
            val name = auth.currentUser!!.displayName?.substringAfter(" ")
            val subject = "Order Received"
            val message = "Dear $name \n\n" +
                    "Your has been placed successfully.\n" +
                    "A dispatcher will deliver the items to you at your requested time. \n\n" +
                    "Regards,\n" +
                    "Order app Team."

            Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    private fun getCurrentLocation(): LatLng? {
        var address: LatLng? = null
        val locationProvider =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                locationProvider.getCurrentLocation(100, null).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val location = it.result
                        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                        val addresses: List<Address>? = try {
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        } catch (e: Exception) {
                            null
                        }
                        address = LatLng(location.latitude, location.longitude)
//                        address = addresses?.get(0)
                    }
                }
            }
        } else
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        return address
    }

    private fun openMap(defaultAddress: LatLng) {
        val fragmentManager = requireActivity().supportFragmentManager
        val mapsFragment =
            MapsFragment(defaultAddress)
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
            .add(android.R.id.content, mapsFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun openTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(30)
            .setTitleText("Schedule delivery")
            .build()
        picker.show(childFragmentManager, "Time Picker")
        picker.addOnPositiveButtonClickListener {
//            TODO: Check out how to get the AM/PM from the time picker
            val hour = picker.hour
            val min = picker.minute
            val AMPM = if (hour >= 12) "PM" else "AM"
            binding.tvDeliveryTime.text = "$hour:$min $AMPM"
        }
    }
}