package com.certified.order.view.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Item
import java.lang.IllegalArgumentException

class ItemDetailsViewModelFactory(val item: Item) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemDetailsViewModel::class.java))
            return ItemDetailsViewModel(item) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}