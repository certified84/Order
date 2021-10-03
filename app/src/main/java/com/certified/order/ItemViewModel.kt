package com.certified.order

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Item

class ItemViewModel(private val itemList: List<Item>) : ViewModel() {

    private val deliveryFee = 1000.00
    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<Double>
    get() = _subtotal
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>>
        get() = _items

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getBurgers()
    }

    private fun getBurgers() {
        Handler(Looper.myLooper()!!).postDelayed({
            _items.value = itemList
            _subtotal.value = deliveryFee
            for (item in itemList)
                _subtotal.value = _subtotal.value?.plus(item.total_price)
            _showProgressBar.value = false
        }, 3000)
    }
}