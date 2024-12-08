package com.sycodes.cryptotrack.instance

import com.sycodes.cryptotrack.services.HomePageCoinApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    val api: HomePageCoinApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HomePageCoinApiService::class.java)
    }
}