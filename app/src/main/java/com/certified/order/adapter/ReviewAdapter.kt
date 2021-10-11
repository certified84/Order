package com.certified.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.databinding.LayoutItemReviewBinding
import com.certified.order.model.Review
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ReviewAdapter(options: FirestoreRecyclerOptions<Review>) :
    FirestoreRecyclerAdapter<Review, ReviewAdapter.ViewHolder>(options) {

    inner class ViewHolder(private val binding: LayoutItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.review = review
            if (Firebase.auth.currentUser?.photoUrl != null)
                Glide.with(itemView)
                    .load(Firebase.auth.currentUser?.photoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivReviewerPicture)
            else
                Glide.with(itemView)
                    .load(R.drawable.no_profile_image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivReviewerPicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Review) {
        holder.bind(model)
    }
}