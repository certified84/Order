package com.certified.order

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
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
                _reviews.value = reviewList
                _showProgressBar.value = false
//            } catch (e: Exception) {
////                TODO: Show Shimmering or something like that design
//        e.message
//            }
//        }
    }
}