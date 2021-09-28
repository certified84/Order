package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutBurgerBinding
import com.certified.order.model.Burger

class OtherBurgerAdapter(val burgers: List<Burger>) :
    ListAdapter<Burger, OtherBurgerAdapter.ViewHolder>(diffCallback) {

    private lateinit var listener: OnBurgerClickedListener

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Burger>() {
            override fun areItemsTheSame(oldItem: Burger, newItem: Burger) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Burger,
                newItem: Burger
            ): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: LayoutBurgerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(burger: Burger) {
            binding.burger = burger
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
            LayoutBurgerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = burgers[position]
            holder.bind(currentItem)
    }

    interface OnBurgerClickedListener {
        fun onBookMarkClick(burger: Burger)
    }

    fun setOnBurgerClickedListener(listener: OnBurgerClickedListener) {
        this.listener = listener
    }

    fun getBookMarkAt(position: Int): Burger {
        return getItem(position)
    }
}