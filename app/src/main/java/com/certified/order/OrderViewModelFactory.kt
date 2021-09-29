package com.certified.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Item

class OrderViewModelFactory(private val burgers: List<Item>?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherBurgerViewModel::class.java))
            return OtherBurgerViewModel(burgers!!) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}