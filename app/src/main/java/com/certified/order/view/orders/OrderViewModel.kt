package com.certified.order.view.orders

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Order

class OrderViewModel(private val orderList: List<Order>) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private var _showEmptyOrderDesign = MutableLiveData<Boolean>()
    val showEmptyOrderDesign: LiveData<Boolean>
        get() = _showEmptyOrderDesign

    init {
        getItems()
    }

    private fun getItems() {
        Handler(Looper.myLooper()!!).postDelayed({

            if (orderList.size >= 1)
                _showEmptyOrderDesign.value = false

            _orders.value = orderList

            _showProgressBar.value = false
        }, 3000)
    }
}