package com.certified.order.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.certified.order.ItemViewModel
import com.certified.order.ItemViewModelFactory
import com.certified.order.R
import com.certified.order.adapter.ItemAdapter
import com.certified.order.databinding.FragmentCompleteOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.certified.order.util.Config
import com.certified.order.util.Mailer
import com.flutterwave.raveandroid.RavePayManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class CompleteOrderFragment(private val items: List<Item>) : DialogFragment() {

    private lateinit var binding: FragmentCompleteOrderBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteOrderBinding.inflate(layoutInflater)

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            val userRef =
                db.collection("accounts").document("users")
                    .collection(currentUser.uid).document("details")
            userRef.get().addOnSuccessListener {
                if (it.exists()) {
                    tvReceiverPhone.text = it.getString("phone")!!
                    tvAddress.text = it.getString("default_address_line")
                }
            }

            tvReceiverName.text = currentUser.displayName

            if (profileImage == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(receiverProfileImage)
            else
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)

//            tvAddress.setOnClickListener {
//                val latitude = getCurrentLocation()?.latitude
//                val longitude = getCurrentLocation()?.longitude
//                val position = latitude?.let { it1 -> longitude?.let { it2 -> LatLng(it1, it2) } }
//                getCurrentLocation()?.let { it1 -> openMap(it1) }
//            }
            tvDeliveryTime.setOnClickListener { openTimePicker() }

            btnCompleteOrder.setOnClickListener {
                if (tvDeliveryTime.text != "00:00 AM") {
                    if (tvAddress.text == resources.getString(R.string.click_here_to_set_delivery_address)) {

                        progressBar.visibility = View.VISIBLE

                        val newOrder = Order(
                            currentUser.displayName!!,
                            currentUser.photoUrl,
                            tvReceiverPhone.text.toString(),
                            tvSubtotal.text.toString().toDouble(),
                            items
                        )
                        newOrder.deliveryTime = tvDeliveryTime.text.toString()

//                        TODO: Process the order with either Gpay or Flutterwave. Only make use of one for now
//                        TODO: If the oder was completed successfully, replace the if
//                        if (true) {
                        val ordersRef = db.collection("orders").document(currentUser.uid)
                        newOrder.id = ordersRef.id
                        ordersRef.set(newOrder).addOnCompleteListener {
                            if (it.isSuccessful) {
                                progressBar.visibility = View.GONE
                                val cartRef =
                                    db.collection("cart").document(currentUser.uid)
                                        .collection("my_cart_items")
                                cartRef.get().addOnSuccessListener {
//                                    TODO: Delete all items in the users cart
//                                    TODO: progressBar.Visibility = View.Gone
                                }
                                super.dismiss()
                            }
                        }
//                        }
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

//            val raveUiManager =
//                RavePayManager(requireActivity()).setAmount(tvSubtotal.text.toString().toDouble())
//                    .setCurrency("NGN")
//                    .setfName(currentUser.displayName?.substringBefore(" "))
//                    .setlName(currentUser.displayName?.substringAfter(" "))
//                    .setEmail(currentUser.email)
        }
    }

    private fun mailAdmin() {
        binding.apply {
            val email = Config.ADMIN_EMAIL
            val subject = "New Order received"
            val message =
                "A new order has been placed. \nCheck out more information in Firebase console.\n"

            Mailer.sendMail(email, subject, message).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    private fun mailUser() {
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
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 101)
//            getCurrentLocation()
//    }

    private fun getCurrentLocation(): LatLng? {

        var address: LatLng? = null
//        CoroutineScope(Dispatchers.IO).launch {
        val locationProvider =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvider.getCurrentLocation(100, object : CancellationToken() {
                override fun isCancellationRequested(): Boolean {
                    return false
                }

                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    TODO("Not yet implemented")
                }

            }).addOnCompleteListener {
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
            locationProvider.lastLocation.addOnCompleteListener {
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
        } else
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
//        }
        return address
    }

    private fun openMap(defaultAddress: LatLng) {
        val fragmentManager = requireActivity().supportFragmentManager
        val mapsFragment =
            if (defaultAddress != null) MapsFragment(defaultAddress) else MapsFragment(
                LatLng(-34.0, 151.0)
            )
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
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Schedule delivery")
            .build()
//        In activity use supportFragmentManager
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