package com.certified.order.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
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
import co.paystack.android.PaystackSdk.applicationContext
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
import com.certified.order.databinding.FragmentConfirmOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.certified.order.util.Config
import com.certified.order.util.Mailer
import com.certified.order.util.PreferenceKeys
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
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

class ConfirmOrderFragment(private val items: List<Item>, private val from: String) :
    DialogFragment() {

    init {
        Log.d("TAG", "ConfirmOrderFragment: $from")
    }

    private lateinit var binding: FragmentConfirmOrderBinding
    private lateinit var auth: FirebaseAuth
    private var currentLatLng: LatLng? = null
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmOrderBinding.inflate(layoutInflater)

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
                binding.progressBar.visibility = View.VISIBLE
                requestCurrentLocation()
            }
            tvDeliveryTime.setOnClickListener { openTimePicker() }

            btnCloseDialog.setOnClickListener { dismiss() }
            btnConfirmOrder.setOnClickListener {

                if (tvDeliveryTime.text != "00:00 AM") {
                    if (tvAddress.text != resources.getString(R.string.click_here_to_set_delivery_address) && tvAddress.text.isNotEmpty()) {

                        val subtotal = tvSubtotal.text.toString()
                        val newOrder = Order(
                            tvReceiverPhone.text.toString(),
                            subtotal.toInt(),
                            items
                        )
                        newOrder.delivery_time = tvDeliveryTime.text.toString()
                        newOrder.latitude = currentLatLng?.latitude.toString()
                        newOrder.longitude = currentLatLng?.longitude.toString()

//                        Launch a dialog for the user to enter card details
                        val cardDetailsDialog =
                            DialogCardDetailsBinding.inflate(
                                layoutInflater,
                                ConstraintLayout(requireContext()),
                                false
                            )
                        val bottomSheetDialog =
                            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
                        cardDetailsDialog.apply {

//                            Load the test card details into the edit text fields
                            etCardNumber.setText(TEST_CARD_NUMBER)
                            etCardExpiryDate.setText(TEST_CARD_EXPIRY_DATE)
                            etCardCvv.setText(TEST_CARD_CVV)
                            btnConfirmPayment.text = "Pay #$subtotal"

                            btnConfirmPayment.setOnClickListener {
                                if (etCardNumber.text.toString()
                                        .isNotEmpty() && etCardExpiryDate.text.toString()
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
                                        charge.amount = subtotal.toInt() * 100
                                        charge.email = auth.currentUser?.email
                                        charge.card = card

//                                        When the card details entered is valid, Use PaystackSdk to collect payment
                                        PaystackSdk.chargeCard(
                                            requireActivity(),
                                            charge,
                                            object : Paystack.TransactionCallback {
                                                override fun onSuccess(transaction: Transaction?) {

//                                                    When the payment is successful, Save the order details in firestore
//                                                    Save the order in general orders for dispatchers to be to access easily
                                                    val db = Firebase.firestore
                                                    val ordersRef =
                                                        db.collection("all_orders").document()
                                                    newOrder.id = ordersRef.id
                                                    ordersRef.set(newOrder)

//                                                    Save the order in my_orders for the users access only
                                                    val myOrdersRef =
                                                        db.collection("user_orders")
                                                            .document(auth.currentUser!!.uid)
                                                            .collection("my_orders")
                                                            .document()
                                                    myOrdersRef.set(newOrder)

//                                                    If the user places the order from the cart, Delete all the items in the users cart
                                                    if (from == "Cart") {
                                                        db.collection("cart")
                                                            .document(Firebase.auth.currentUser!!.uid)
                                                            .delete().addOnCompleteListener {
                                                                if (it.isSuccessful) {
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        "Order placed successfully",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    bottomSheetDialog.dismiss()
                                                                    dismiss()
                                                                } else {
                                                                    Toast.makeText(
                                                                        requireContext(),
                                                                        "An error occurred: ${it.exception?.message}",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            }
                                                    }
                                                    progressBar.visibility = View.GONE
//                                                    mailAdmin()
//                                                    mailUser()
                                                }

                                                override fun beforeValidate(transaction: Transaction?) {
//                                                    TODO("Not yet implemented")
                                                }

                                                override fun onError(
                                                    error: Throwable?,
                                                    transaction: Transaction?
                                                ) {
                                                    progressBar.visibility = View.GONE
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "An error occurred: ${error?.localizedMessage}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            })
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
                    } else
                        Toast.makeText(
                            requireContext(),
                            "Please set your delivery address",
                            Toast.LENGTH_LONG
                        ).show()
                } else
                    Toast.makeText(
                        requireContext(),
                        "Please set a delivery time",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
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
            val name = auth.currentUser!!.displayName?.substringBefore(" ")
            val subject = "Order Received"
            val message = "Dear $name \n\n" +
                    "Your order has been placed successfully.\n" +
                    "A dispatcher will deliver the items to you at your requested time. \n\n" +
                    "Regards,\n" +
                    "Order app Team."

            Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

    private fun requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Main code
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

            CoroutineScope(Dispatchers.IO).launch {
                currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                    val result = if (task.isSuccessful) {
                        val result: Location = task.result
                        currentLatLng = LatLng(result.latitude, result.longitude)
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address>? = try {
                            geocoder.getFromLocation(result.latitude, result.longitude, 1)
                        } catch (e: Exception) {
                            null
                        }
                        binding.tvAddress.text = addresses?.get(0)?.getAddressLine(0)
                        "Location (success): ${result.latitude}, ${result.longitude}"
                    } else {
                        val exception = task.exception
                        Toast.makeText(
                            requireContext(),
                            "Unable to get location: $exception",
                            Toast.LENGTH_LONG
                        ).show()
                        "Location (failure): $exception"
                    }
                    Log.d("TAG", "getCurrentLocation() result: $result")
                    binding.progressBar.visibility = View.GONE
                }
            }
        } else
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
    }

    private fun openMap(defaultAddress: LatLng) {
//        TODO: Method for user to select location from map
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
            val hour = picker.hour
            val min = picker.minute
            val AMPM = if (hour >= 12) "PM" else "AM"
            binding.tvDeliveryTime.text = "$hour:$min $AMPM"
        }
    }

    companion object {
        private val TEST_CARD_NUMBER = "4084084084084081"
        private val TEST_CARD_CVV = "408"
        private val TEST_CARD_EXPIRY_DATE = "10/22"
    }
}