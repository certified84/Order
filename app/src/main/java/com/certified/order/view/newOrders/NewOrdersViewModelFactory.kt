package com.certified.order.view.newOrders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NewOrdersViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewOrdersViewModel::class.java))
            return NewOrdersViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}