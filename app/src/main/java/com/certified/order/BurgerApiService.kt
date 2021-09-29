package com.certified.order

import com.certified.order.model.Item
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.IOException


private const val BASE_URL = "https://rickandmortyapi.com/"

interface BurgerApiService {

    @GET("api/character")
    suspend fun getBurgers(): BurgerApiResponse

    suspend fun load(): BurgerApiResponse = load()
}

private fun load(): BurgerApiResponse? {
    var burgerApiResponse: BurgerApiResponse? = null

    CoroutineScope(Dispatchers.IO).launch {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://burgers1.p.rapidapi.com/burgers")
            .get()
            .addHeader("x-rapidapi-host", "burgers1.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "34b9be6a3emshe7e3e482b50a80fp19ecadjsn77367a819a5b")
            .build()

        val response = client.newCall(request).execute()
        val gson = GsonBuilder().create()
        val okhttpResponse = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                burgerApiResponse =
                    gson.fromJson(response.body().toString(), BurgerApiResponse::class.java)
            }

        })
    }
    return burgerApiResponse
}

private val moshi
    get() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit: Retrofit
    get() = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

object BurgerApi {
    val apiService: BurgerApiService by lazy { retrofit.create(BurgerApiService::class.java) }
//    val api: BurgerApiService by lazy { BurgerApiService }
}

class BurgerApiResponse(val results: List<Item>)