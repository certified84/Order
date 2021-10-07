package com.certified.order.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentOrderDetailsBinding
import com.certified.order.model.Order
import com.google.android.gms.maps.model.LatLng

class OrderDetailsFragment(private val order: Order) : DialogFragment() {

    private lateinit var binding: FragmentOrderDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            loadOrderDetails(order)

//            if (order.dispatcher_name != "")
                chipItemStatus.setOnClickListener {
                    when (chipItemStatus.text.toString()) {
                        "Pending" -> {
                            chipItemStatus.text = resources.getString(R.string.delivered)
                            chipItemStatus.isChecked = true

                            chipItemStatus.isClickable = false

//                            TODO: Update the order details in firestore
                        }
                        "Delivered" -> chipItemStatus.isClickable = false
                    }
                }
        }
    }

    private fun loadOrderDetails(order: Order) {
        binding.apply {
            val name = order.receiver_name
            val profileImage = order.receiver_photourl
            val phoneNo = order.receiver_phone_no
            val latitude = order.latitude?.toDouble()
            val longitude = order.latitude?.toDouble()
            val isDelivered = order.isDelivered
            val deliveryTime = order.deliveryTime
            val subTotal = order.subtotal

            if (profileImage != null)
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)
            else {
                Glide.with(requireContext())
                    .load(R.drawable.no_profile_image)
                    .into(receiverProfileImage)
            }

            tvReceiverName.text = name
            tvDeliveryTime.text = deliveryTime
            tvSubtotal.text = subTotal.toString()

            if (isDelivered)
                chipItemStatus.text = resources.getString(R.string.delivered)
            else
                chipItemStatus.text = resources.getString(R.string.pending)

            btnCallReceiver.setOnClickListener {
//                TODO: open the dialer with the receiver's phone
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel: $phoneNo")))
            }

            loadDirections(LatLng(latitude!!, longitude!!))
        }
    }

    private fun getCurrentLocation(): LatLng? {
        return null
    }

    private fun loadDirections(latLng: LatLng) {
//            TODO: Load the delivery direction into the fragment
        val fragmentManager = requireActivity().supportFragmentManager
        val mapsFragment = MapsFragment(latLng)
//        mapsFragment.show(fragmentManager, "loginFragment")
        val transaction = fragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction
//            .replace(R.id.fragment_location, mapsFragment)
            .add(R.id.fragment_location, mapsFragment)
            .addToBackStack(null)
            .commit()
    }
}