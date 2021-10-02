package com.certified.order.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.LayoutItemDetailsBinding
import com.certified.order.model.Item
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailsFragment(val type: String, val item: Item, val uid: String) : DialogFragment() {

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

        binding.item = item

        var quantity = item.quantity.toInt()
        checkQuantity(quantity)
//            TODO: Save the quantity in a viewholder to prevent loss on configuration change

        binding.apply {

            root.setOnClickListener { dismiss() }
            btnIncreaseQuantity.setOnClickListener {
                quantity++
                checkQuantity(quantity)
            }
            btnDecreaseQuantity.setOnClickListener {
                quantity--
                checkQuantity(quantity)
            }

            if (type == "my orders" || type == "cart") {
                btnBuyNow.visibility = View.GONE
                btnAddToCart.visibility = View.GONE
            } else if (type == "home") {
                Glide.with(requireContext())
                    .load(R.drawable.burger_image_3)
                    .into(itemImage)
            }

            btnAddToCart.setOnClickListener {
                progressBar2.visibility = View.VISIBLE
                val newItem = item
                newItem!!.quantity = binding.tvItemQuantity.text.toString()
                newItem.total_price = newItem.quantity.toInt() * newItem.price
                val db = Firebase.firestore
                val ordersRef =
                    db.collection("cart").document(uid).collection("my_cart_items").document()
                ordersRef.set(newItem).addOnCompleteListener {
                    progressBar2.visibility = View.GONE
                    dismiss()
                }
            }
        }
    }

    private fun checkQuantity(quantity: Int) {
        binding.apply {
            tvItemQuantity.text = "$quantity"
            if (quantity <= 1) {
                binding.btnDecreaseQuantity.alpha = 0.4f
                binding.btnDecreaseQuantity.isClickable = false
            } else {
                binding.btnDecreaseQuantity.alpha = 1f
                binding.btnDecreaseQuantity.isClickable = true
            }
        }
    }
}