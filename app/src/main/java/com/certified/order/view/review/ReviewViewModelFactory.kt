package com.certified.order.view.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Review

class ReviewViewModelFactory(private val reviews: List<Review>?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java))
            return ReviewViewModel(reviews!!) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}