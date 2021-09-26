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
import com.certified.order.BurgerViewModel
import com.certified.order.BurgerViewModelFactory
import com.certified.order.OtherBurgerViewModel
import com.certified.order.R
import com.certified.order.adapter.BurgerAdapter
import com.certified.order.adapter.OtherBurgerAdapter
import com.certified.order.databinding.FragmentCompleteOrderBinding
import com.certified.order.model.Burger
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class CompleteOrderFragment(private val burgers: List<Burger>) : DialogFragment() {

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

        val viewModelFactory = BurgerViewModelFactory(null, burgers)
        val viewModel: OtherBurgerViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(OtherBurgerViewModel::class.java)
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

        val adapter = OtherBurgerAdapter(burgers)
        binding.recyclerViewItemsToDeliver.adapter = adapter

        binding.apply {

//            val currentUser = auth.currentUser!!
            val db = Firebase.firestore

//            val adapter = OtherBurgerAdapter(burgers)
//            recyclerViewItemsToDeliver.adapter = adapter
//            recyclerViewItemsToDeliver.layoutManager = LinearLayoutManager(requireContext())

//            TODO: Obtain the current device location
            val deliveryAddress = getCurrentLocation()
//            val profileImage = currentUser.photoUrl
            tvAddress.text = "Click here to set delivery address"
            tvReceiverPhone.text = "08136108482"
            tvReceiverName.text = "Samson Achiaga"

//            val userRef =
//                db.collection("accounts").document("users")
//                    .collection(currentUser.uid).document("details")
//            userRef.get().addOnSuccessListener {
//                if (it.exists()) {
//                    tvReceiverPhone.text = it.getString("phone")!!
//                    tvAddress.text = it.getString("default_address_line")
//                }
//            }

            val deliveryFee = 1000.00
            var subtotal = deliveryFee
            for (burger in burgers) {
//                subtotal += item.price
                subtotal += burger.total_price
            }

            tvSubtotal.text = "#$subtotal"
//            tvReceiverName.text = currentUser.displayName

//            if (profileImage == null)
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(receiverProfileImage)
//            else
//                Glide.with(requireContext())
//                    .load(profileImage)
//                    .into(receiverProfileImage)

            tvAddress.setOnClickListener {
                val latitude = getCurrentLocation()?.latitude
                val longitude = getCurrentLocation()?.longitude
                val position = latitude?.let { it1 -> longitude?.let { it2 -> LatLng(it1, it2) } }
                openMap(position)
            }
            tvDeliveryTime.setOnClickListener { openTimePicker() }
            btnCompleteOrder.setOnClickListener {
                if (tvDeliveryTime.text != "00:00 AM") {
                    if (tvAddress.text != resources.getString(R.string.click_here_to_set_delivery_address)) {

                        progressBar.visibility = View.VISIBLE

                        val newOrder = Order(
                            null,
                            null,
                            deliveryAddress,
                            "08136108482",
                            subtotal,
                            burgers
                        )
                        newOrder.delivery_time = tvDeliveryTime.text.toString()

//                        TODO: Process the order with either Gpay or Flutterwave. Only make use of one for now
//                        TODO: If the oder was completed successfully, replace the if
//                        if (true) {
//                            val ordersRef = db.document("orders")
//                            ordersRef.set(newOrder).addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    val cartRef = db.collection("cart").document(currentUser.uid)
//                                    cartRef.get().addOnSuccessListener {
////                                TODO: Delete all items in the users cart
//                        TODO: progressBar.Visibility = View.Gone
//                                    }
//                                    super.dismiss()
//                                }
//                            }
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
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101)
            getCurrentLocation()
    }

    private fun getCurrentLocation(): Address? {
        var address: Address? = null
        val locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvider.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result
                    val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    address = addresses[0]
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

    private fun openMap(defaultAddress: LatLng?) {
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
            .setTimeFormat(clockFormat)
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