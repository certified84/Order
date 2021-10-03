package com.certified.order.view.my_orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.certified.order.R
import com.certified.order.databinding.FragmentPendingOrdersBinding

class PendingOrdersFragment : Fragment() {

    private lateinit var binding: FragmentPendingOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPendingOrdersBinding.inflate(layoutInflater)

        return binding.root
    }
}