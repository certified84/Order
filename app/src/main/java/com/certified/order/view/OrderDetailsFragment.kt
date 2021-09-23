package com.certified.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.certified.order.R
import com.certified.order.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment(val accountType: String, val from: String?) : DialogFragment() {

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

            if (accountType == "user" && from != null) {
//                If from is not null then we are in the CartFragment
                chipItemStatus.isClickable = false
                btnCallReceiver.visibility = View.GONE
                tvAddress.setOnClickListener {

                }

            } else if (accountType == "user" && from == null){

                btnCompleteOrder.visibility = View.GONE

            } else if (accountType == "dispatcher" && from == null) {

                tvReceiverPhone.visibility = View.GONE
                tvSubtotal.visibility = View.GONE
                tvSubtotalTitle.visibility = View.GONE

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
    }
}