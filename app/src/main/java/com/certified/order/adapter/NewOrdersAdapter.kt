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
import com.certified.order.databinding.LayoutOrderBinding
import com.certified.order.model.Item
import com.certified.order.model.Order

class NewOrdersAdapter(private val order: List<Order>) :
    ListAdapter<Order, NewOrdersAdapter.ViewHolder>(diffCallback) {

    private lateinit var listener: OnOrderClickedListener

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Order,
                newItem: Order
            ): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: LayoutOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //        TODO: Replace the burger with an item
        fun bind(order: Order) {
            binding.order = order
            binding.executePendingBindings()
            binding.apply {
            }
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = order[position]
        holder.bind(currentItem)
    }

    interface OnOrderClickedListener {
        fun onOrderClick(order: Order)
    }

    fun setOnOrderClickedListener(listener: OnOrderClickedListener) {
        this.listener = listener
    }
}