package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutItemCartBinding
import com.certified.order.model.Item
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtherCartAdapter(options: FirestoreRecyclerOptions<Item>) :
    FirestoreRecyclerAdapter<Item, OtherCartAdapter.ViewHolder>(
        options
    ) {

//    companion object {
//        private val options: FirestoreRecyclerOptions<Item> =
//            FirestoreRecyclerOptions.Builder<Item>()
//                .setQuery(
//                    Firebase.firestore.collection("cart")
//                        .document(Firebase.auth.currentUser!!.uid).collection("my_cart_items"),
//                    Item::class.java
//                ).build()
//    }

    inner class ViewHolder(private val binding: LayoutItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(burger: Item) {
            binding.item = burger
            var quantity = burger.quantity.toInt()
            checkQuantity(quantity)
//            binding.executePendingBindings()
            binding.apply {
                when (item!!.type) {
                    "burger" -> Glide.with(itemView)
                        .load(R.drawable.burger_image_3)
                        .into(itemImage)
                    "shawarma" -> Glide.with(itemView)
                        .load(R.drawable.shawarma_image)
                        .into(itemImage)
                    "pizza" -> Glide.with(itemView)
                        .load(R.drawable.pizza_image)
                        .into(itemImage)
                    "chicken and chips" -> Glide.with(itemView)
                        .load(R.drawable.chicken_and_chips_image)
                        .into(itemImage)
                }
                btnIncreaseQuantity.setOnClickListener {
                    quantity++
                    checkQuantity(quantity)
                }
                btnDecreaseQuantity.setOnClickListener {
                    quantity--
                    checkQuantity(quantity)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Item) {
        holder.bind(model)
    }
}