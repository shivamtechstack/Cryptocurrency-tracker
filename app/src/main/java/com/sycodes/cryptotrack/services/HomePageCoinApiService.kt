package com.sycodes.cryptotrack.services

import com.sycodes.cryptotrack.model.CoinDataHomePage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomePageCoinApiService {
    @GET("coins/markets")
    fun getCoinMarket(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order : String,
        @Query("per_page") perPage : Int = 20,
        @Query("page") page : Int =1,
        @Query("sparkline") sparkline : Boolean = false,
    ): Call<List<CoinDataHomePage>>
}