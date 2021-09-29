package com.certified.order

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.certified.order.databinding.ActivityMainBinding
import com.certified.order.util.PreferenceKeys
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        println(load())

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        isDarkModeEnabled()
    }

    private fun isDarkModeEnabled() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (preferences.getInt(PreferenceKeys.DARK_MODE, 0)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun getCurrentLocation(): Address? {
        var address: Address? = null
        val locationProvider = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    val location = it.result
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    address =  addresses[0]
                }
            }
        } else
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
        return address
    }

    fun load(): BurgerApiResponse? {
        var burgerApiResponse: BurgerApiResponse? = null

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://burgers1.p.rapidapi.com/burgers")
                .get()
                .addHeader("x-rapidapi-host", "burgers1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "34b9be6a3emshe7e3e482b50a80fp19ecadjsn77367a819a5b")
                .build()

//        val response = client.newCall(request).execute()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println(e.message)
                    Log.d("Request", "onFailure: ${e.message}")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val body = response?.body().toString()
                    val gson = GsonBuilder().create()
                    burgerApiResponse = gson.fromJson(body, BurgerApiResponse::class.java)
                    println(body)
                    Log.d("Request", "onFailure: $body")
                }

            })
        }
        println(burgerApiResponse)
        return burgerApiResponse
    }
}