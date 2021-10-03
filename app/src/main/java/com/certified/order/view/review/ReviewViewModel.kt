package com.certified.order.view.review

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Review

class ReviewViewModel(private val reviewList: List<Review>) : ViewModel() {

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>>
        get() = _reviews

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getBurgers()
    }

    private fun getBurgers() {
        Handler(Looper.myLooper()!!).postDelayed({
            _reviews.value = reviewList
            _showProgressBar.value = false
        }, 5000)
    }
}