package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certified.order.model.Item

class OtherBurgerViewModel(private val burgerList: List<Item>) : ViewModel() {

    private val _burgers = MutableLiveData<List<Item>>()
    val burgers: LiveData<List<Item>>
        get() = _burgers

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getBurgers()
    }

    private fun getBurgers() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
                _burgers.value = burgerList
                _showProgressBar.value = false
//            } catch (e: Exception) {
////                TODO: Show Shimmering or something like that design
//        e.message
//            }
//        }
    }
}