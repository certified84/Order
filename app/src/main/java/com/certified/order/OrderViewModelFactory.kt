package com.certified.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Order

class OrderViewModelFactory(private val orders: List<Order>?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java))
            return OrderViewModel(orders!!) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}