package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.order.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsViewModel(private val apiService: ApiService) : ViewModel() {

    private val _characters = MutableLiveData<List<Item>>()
    val characters: LiveData<List<Item>>
        get() = _characters

    private var _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    init {
        getCharacters()
    }

    private fun getCharacters() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _characters.value = apiService.getCharacters().results
                _showProgressBar.value = false
            } catch (e: Exception) {
//                TODO: Show Shimmering or something like that design
            }
        }
    }
}