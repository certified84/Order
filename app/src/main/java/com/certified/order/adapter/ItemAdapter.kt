package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.databinding.LayoutItemBinding
import com.certified.order.model.Item

class ItemAdapter(val items: List<Item>) :
    ListAdapter<Item, ItemAdapter.ViewHolder>(diffCallback) {

    private lateinit var listener: OnItemClickedListener

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
            Glide.with(itemView)
                .load(item.picture)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemImage)
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
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    interface OnItemClickedListener {
        fun onItemClick(item: Item)
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }
}