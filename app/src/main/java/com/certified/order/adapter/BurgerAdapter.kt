package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutItemBinding
import com.certified.order.model.Item

class BurgerAdapter(val burgers: List<Item>) :
    ListAdapter<Item, BurgerAdapter.ViewHolder>(diffCallback) {

    private lateinit var listener: OnBurgerClickedListener

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

        fun bind(burger: Item) {
            binding.item = burger
            binding.executePendingBindings()
            binding.apply {
                Glide.with(itemView)
                    .load(R.drawable.burger_image_3)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemImage)
//                tvItemName.text = burger.name
//                tvItemDesc.text = burger.description
//                tvItemPrice.text = burger.price.toString()
            }
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookMarkClick(getItem(position))
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
        val currentItem = burgers[position]
        holder.bind(currentItem)
    }

    interface OnBurgerClickedListener {
        fun onBookMarkClick(burger: Item)
    }

    fun setOnBurgerClickedListener(listener: OnBurgerClickedListener) {
        this.listener = listener
    }

    fun getBookMarkAt(position: Int): Item {
        return getItem(position)
    }
}