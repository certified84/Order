package com.certified.order.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.FragmentItemDetailsBinding
import com.certified.order.model.Item
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemDetailsFragment(val type: String, val item: Item, val uid: String) : DialogFragment() {

    private lateinit var binding: FragmentItemDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var quantity = item.quantity.toInt()
        checkQuantity(quantity)

        val viewModelFactory = ItemDetailsViewModelFactory(item)
        val viewModel: ItemDetailsViewModel by lazy {
            ViewModelProvider(
                viewModelStore,
                viewModelFactory
            ).get(ItemDetailsViewModel::class.java)
        }

        viewModel.showProgressBar.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.apply {

            root.setOnClickListener { super.dismissAllowingStateLoss() }
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
                when (item.type) {
                    "burger" -> Glide.with(requireContext())
                        .load(R.drawable.burger_image)
                        .into(itemImage)
                    "shawarma" -> Glide.with(requireContext())
                        .load(R.drawable.shawarma_image)
                        .into(itemImage)
                    "pizza" -> Glide.with(requireContext())
                        .load(R.drawable.pizza_image)
                        .into(itemImage)
                    "chicken and chips" -> Glide.with(requireContext())
                        .load(R.drawable.chicken_and_chips_image)
                        .into(itemImage)
                }
            }

            btnAddToCart.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                val newItem = item
                newItem.quantity = binding.tvItemQuantity.text.toString()
                newItem.total_price = newItem.quantity.toInt() * newItem.price
                val db = Firebase.firestore
                val ordersRef =
                    db.collection("cart").document(uid).collection("my_cart_items").document()
                newItem.id = ordersRef.id
                ordersRef.set(newItem).addOnCompleteListener {
                    progressBar.visibility = View.GONE
                    dismiss()
                }
            }

            btnBuyNow.setOnClickListener {
//                TODO: Process the order
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