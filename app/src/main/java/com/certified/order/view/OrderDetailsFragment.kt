package com.certified.order.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentOrderDetailsBinding
import com.certified.order.model.Order

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

            val receiver = order.receiver
            tvReceiverName.text = receiver.displayName

            val profileImage = receiver.photoUrl
            if (profileImage != null)
                Glide.with(requireContext())
                    .load(profileImage)
                    .into(receiverProfileImage)
            else {
//                TODO: Load a default image
            }

            if (order.isDelivered)
                chipItemStatus.text = resources.getString(R.string.delivered)
            else
                chipItemStatus.text = resources.getString(R.string.pending)
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

            tvDeliveryTime.text = order.delivery_time
            tvSubtotal.text = order.subtotal.toString()

            btnCallReceiver.setOnClickListener {
//                TODO: open the dialer with the receiver's phone
            }
        }
    }
}