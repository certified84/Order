package com.certified.order.view

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentCompleteOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        binding.apply {

            val currentUser = auth.currentUser!!
            val db = Firebase.firestore

//            TODO: Obtain the current device location
            val deliveryAddress = LatLng(-23.45, 25.45)
            val profileImage = currentUser.photoUrl

            val userRef =
                db.collection("accounts").document("users")
                    .collection(currentUser.uid).document("details")
            userRef.get().addOnSuccessListener {
                if (it.exists()) {
                    tvReceiverPhone.text = it.getString("phone")!!
                }
            }

            val deliveryFee = 1000.00
            var subtotal = 0.00
            for (item in items) {
                subtotal += item.price
            }
            tvSubtotal.text = "#${deliveryFee + subtotal}"
            tvReceiverName.text = currentUser.displayName
//            tvReceiverPhone.text = receiverPhone
            if (profileImage == null)
//                TODO: Load default picture
            else
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)

            tvAddress.setOnClickListener { openMap(deliveryAddress) }
            tvDeliveryTime.setOnClickListener { openTimePicker() }
            btnCompleteOrder.setOnClickListener {
                if (tvDeliveryTime.text != "00:00 AM") {
                    if (tvAddress.text != resources.getString(R.string.click_here_to_set_delivery_address)) {

                        val newOrder = Order(currentUser, null, deliveryAddress, "08136108482", subtotal, items)
                        newOrder.delivery_time = tvDeliveryTime.text.toString()

//                        TODO: Process the order with either Gpay or Flutterwave. Only make use of one for now
//                        TODO: If the oder was completed successfully, replace the if
                        if (true) {
                            val ordersRef = db.document("orders")
                            ordersRef.set(newOrder).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val cartRef = db.collection("cart").document(currentUser.uid)
                                    cartRef.get().addOnSuccessListener {
//                                TODO: Delete all items in the users cart
                                    }
                                    super.dismiss()
                                }
                            }
                        }
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

    private fun openMap(defaultAddress: LatLng?) {
        val isLargeLayout = resources.getBoolean(R.bool.large_layout)
        val fragmentManager = requireActivity().supportFragmentManager
        val mapsFragment = if (defaultAddress != null) MapsFragment(defaultAddress) else MapsFragment(LatLng(-34.0, 151.0))
        // The device is smaller, so show the fragment fullscreen
        val transaction = fragmentManager.beginTransaction()
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
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
            binding.tvDeliveryTime.text = "$hour:$min AM"
        }
    }
}