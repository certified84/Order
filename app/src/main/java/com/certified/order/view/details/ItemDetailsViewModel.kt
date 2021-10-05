package com.certified.order.view.details

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Item

class ItemDetailsViewModel(val item: Item) : ViewModel() {

    private val _itemName = MutableLiveData<String>()
    val itemName: LiveData<String>
        get() = _itemName

    private val _itemDescription = MutableLiveData<String>()
    val itemDescription: LiveData<String>
        get() = _itemDescription

    private val _itemQuantity = MutableLiveData<Int>()
    val itemQuantity: LiveData<Int>
        get() = _itemQuantity

    private val _itemPrice = MutableLiveData<Int>()
    val itemPrice: LiveData<Int>
        get() = _itemPrice

    private var _showProgressBar = MutableLiveData<Boolean>(true)
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getItem()
    }

    private fun getItem() {
            _itemName.value = item.name
            _itemDescription.value = item.description
            _itemQuantity.value = item.quantity.toInt()
            _itemPrice.value = item.price
            _showProgressBar.value = false
    }

    private fun changeQuantity() {

    }
}