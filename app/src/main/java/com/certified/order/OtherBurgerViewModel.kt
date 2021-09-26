package com.certified.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certified.order.model.Burger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

public class OtherBurgerViewModel(private val burgerList: List<Burger>) : ViewModel() {

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
//            }
//        }
    }

    fun load() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://burgers1.p.rapidapi.com/burgers")
            .get()
            .addHeader("x-rapidapi-host", "burgers1.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "34b9be6a3emshe7e3e482b50a80fp19ecadjsn77367a819a5b")
            .build()

        val response = client.newCall(request).execute()

        CoroutineScope(Dispatchers.IO).launch {
            val burgers = client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}