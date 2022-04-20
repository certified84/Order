package com.certified.order.adapter

import android.util.Log
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
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
            Log.d("TAG", "ViewHolder: created")
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }
    }

    fun filterItems(which: String) {
        val filteredItems = items.filter {
            it.type == which
        }
        submitList(filteredItems)
        notifyDataSetChanged()
    }

    fun allItems() {
        submitList(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("TAG", "onCreateViewHolder: Created")
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val itemToShow = if (which == null) items else items.filter { it.type == which }
        val currentItem = getItem(position)
        holder.bind(currentItem)
        Log.d("TAG", "onBindViewHolder: bind")
    }

    interface OnItemClickedListener {
        fun onItemClick(item: Item)
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }
}