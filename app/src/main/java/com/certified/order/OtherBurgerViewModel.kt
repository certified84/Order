package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.order.model.Burger
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class OtherBurgerViewModel(private val burgerList: List<Burger>) : ViewModel() {

    private val _burgers = MutableLiveData<List<Burger>>()
    val burgers: LiveData<List<Burger>>
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