package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutItemCartBinding
import com.certified.order.model.Item

class CartAdapter(private val items: List<Item>) :
    ListAdapter<Item, CartAdapter.ViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Item,
                newItem: Item
            ): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: LayoutItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(burger: Item) {
            binding.item = burger
            var quantity = burger.quantity.toInt()
            checkQuantity(quantity)
            binding.executePendingBindings()
            binding.apply {
                Glide.with(itemView)
                    .load(R.drawable.burger_image_3)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemImage)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }
}