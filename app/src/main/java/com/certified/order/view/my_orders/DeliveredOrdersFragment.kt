package com.certified.order.view.my_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.certified.order.R
import com.certified.order.databinding.FragmentDeliveredOrdersBinding

class DeliveredOrdersFragment : Fragment() {

    private lateinit var binding: FragmentDeliveredOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDeliveredOrdersBinding.inflate(layoutInflater)


        return binding.root
    }
}