package com.certified.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.certified.order.model.Burger

class BurgerViewModelFactory(private val apiService: BurgerApiService?, private val burgers: List<Burger>?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BurgerViewModel::class.java))
            return BurgerViewModel(apiService!!) as T
        else if (modelClass.isAssignableFrom(OtherBurgerViewModel::class.java))
            return OtherBurgerViewModel(burgers!!) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}