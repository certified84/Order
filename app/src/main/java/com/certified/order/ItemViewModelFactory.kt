package com.certified.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Item

class ItemViewModelFactory(private val items: List<Item>?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java))
            return ItemViewModel(items!!) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}