package com.certified.order

import com.certified.order.model.Burger
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://rickandmortyapi.com/"

interface BurgerApiService {

    @GET("api/character")
    suspend fun getBurgers(): BurgerApiResponse
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
    val apiService : BurgerApiService by lazy { retrofit.create(BurgerApiService::class.java) }
}

class BurgerApiResponse(val results: List<Burger>)