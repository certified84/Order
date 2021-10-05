package com.certified.order.view.my_orders

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

    private var _showProgressBar = MutableLiveData<Boolean>(true)
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private var _showEmptyOrderDesign = MutableLiveData<Boolean>(true)
    val showEmptyOrderDesign: LiveData<Boolean>
        get() = _showEmptyOrderDesign

    init {
        getBurgers()
    }

    private fun getBurgers() {
        Handler(Looper.myLooper()!!).postDelayed({

            _orders.value = orderList

            _showProgressBar.value = false

            if (orderList.isNotEmpty())
                _showEmptyOrderDesign.value = false
            else
                _showProgressBar.value = false

        }, 3000)
    }
}