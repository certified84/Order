package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Item

// TODO: Change the burger to item
class ItemViewModel(private val itemList: List<Item>) : ViewModel() {

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>>
        get() = _items

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getItems()
    }

    private fun getItems() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
                _items.value = itemList
                _showProgressBar.value = false
//            } catch (e: Exception) {
////                TODO: Show Shimmering or something like that design
//        e.message
//            }
//        }
    }
}