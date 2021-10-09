package com.certified.order

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Item

class ItemViewModel(private val itemList: List<Item>) : ViewModel() {

    private val _deliveryFee = MutableLiveData<Double>()
    val deliveryFee: LiveData<Double>
        get() = _deliveryFee

    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<Double>
        get() = _subtotal

    private val _itemTotal = MutableLiveData<Int>()
    val itemTotal: LiveData<Int>
        get() = _itemTotal

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>>
        get() = _items

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private var _showEmptyCartDesign = MutableLiveData<Boolean>()
    val showEmptyCartDesign: LiveData<Boolean>
        get() = _showEmptyCartDesign

    init {
        getBurgers()
    }

    private fun getBurgers() {
        Handler(Looper.myLooper()!!).postDelayed({

            _deliveryFee.value = 500.00
            _items.value = itemList
            _itemTotal.value = 0
            _subtotal.value = _deliveryFee.value

            for (item in itemList)
                _itemTotal.value = _itemTotal.value?.plus(item.total_price)
            _subtotal.value = _itemTotal.value?.let { _subtotal.value?.plus(it) }

            _showProgressBar.value = false

            if (itemList.isNotEmpty())
                _showEmptyCartDesign.value = false
            else
                _showProgressBar.value = false

        }, 3000)
    }
}