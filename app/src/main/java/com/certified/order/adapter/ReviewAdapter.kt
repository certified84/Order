package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutItemReviewBinding
import com.certified.order.model.Review

class ReviewAdapter(private val reviews: List<Review>): ListAdapter<Review, ReviewAdapter.ViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Review,
                newItem: Review
            ): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: LayoutItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.review = review
            binding.executePendingBindings()
            binding.apply {
                Glide.with(itemView)
                    .load(R.drawable.no_profile_image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivReviewerPicture)
//                tvName.text = character.name
//                tvSpecie.text = character.specie
//                tvStatus.text = character.status
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = reviews[position]
        holder.bind(currentItem)
    }
}