package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.certified.order.R
import com.certified.order.databinding.LayoutItemBinding
import com.certified.order.model.Item

class ItemAdapter(val items: List<Item>, val which: String? = null) :
    ListAdapter<Item, ItemAdapter.ViewHolder>(diffCallback) {

    private lateinit var listener: OnItemClickedListener
    private var itemToShow = if(which == null) items else items.filter { it.type == which }

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

    inner class ViewHolder(private val binding: LayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
            binding.executePendingBindings()
            binding.apply {
                if (which != null)
                    groupQuantity.visibility = View.GONE
                when (item.type) {
                    "burger" -> Glide.with(itemView)
                        .load(R.drawable.burger_image)
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
            }
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemToShow[position]
        holder.bind(currentItem)
    }

    interface OnItemClickedListener {
        fun onItemClick(item: Item)
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }
}