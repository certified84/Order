package com.certified.order.adapter

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.certified.order.R
import com.certified.order.model.Item

//@BindingAdapter("listNewOrders")
//fun bindNewOrdersRecyclerView(recyclerView: RecyclerView, data: List<Order>?) {
//    val adapter = recyclerView.adapter as CompletedOrdersAdapter
//    adapter.submitList(data)
//}

@BindingAdapter("listItems")
fun bindItemRecyclerView(recyclerView: RecyclerView, data: List<Item>?) {
    val adapter = recyclerView.adapter as ItemAdapter
    adapter.submitList(data)
}

//@BindingAdapter("listCartItems")
//fun bindCartRecyclerView(recyclerView: RecyclerView, data: List<Item>?) {
//    val adapter = recyclerView.adapter as CartAdapter
//    adapter.submitList(data)
//}

//@BindingAdapter("listReviews")
//fun bindReviewRecyclerView(recyclerView: RecyclerView, data: List<Review>?) {
//    val adapter = recyclerView.adapter as ReviewAdapter
//    adapter.submitList(data)
//}

@BindingAdapter("imageUrl")
fun bindProfileImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        val burger = R.drawable.burger_image
        if (imgUri == null)
            Glide.with(imgView.context)
                .load(R.drawable.no_profile_image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgView)
        else
            Glide.with(imgView.context)
                .load(imgUri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgView)
    }
}