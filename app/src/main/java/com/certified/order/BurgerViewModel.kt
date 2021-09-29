package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.order.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BurgerViewModel(private val apiService: BurgerApiService) : ViewModel() {

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _burgers.value = apiService.load().results
                _showProgressBar.value = false
            } catch (e: Exception) {
//                TODO: Show Shimmering or something like that design
            }
        }
    }
}