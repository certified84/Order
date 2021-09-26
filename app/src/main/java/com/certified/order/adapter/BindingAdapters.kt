package com.certified.order.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.model.Burger

@BindingAdapter("listData")
    fun bindRecyclerView(recyclerView: RecyclerView, data: List<Burger>?) {
        val adapter = recyclerView.adapter as OtherBurgerAdapter
        adapter.submitList(data)
    }

    @BindingAdapter("imageUrl")
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            val burger = R.drawable.burger_image_3
            Glide.with(imgView.context)
                .load(burger)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgView)
        }
    }