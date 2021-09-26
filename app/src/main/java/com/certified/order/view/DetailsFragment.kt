package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.certified.order.databinding.LayoutItemDetailsBinding

class DetailsFragment(val type: String) : DialogFragment() {

    private lateinit var binding: LayoutItemDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutItemDetailsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (type == "my orders" || type == "cart") {
                btnBuyNow.visibility = View.GONE
                btnAddToCart.visibility = View.GONE
            } else if (type == "home") {
            }
        }
    }
}