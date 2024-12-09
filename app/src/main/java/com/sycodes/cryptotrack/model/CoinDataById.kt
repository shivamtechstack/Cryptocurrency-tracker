package com.sycodes.cryptotrack.model

data class CoinDataById(
    var id : String,
    var name : String,
    var symbol : String,
    var description: Map<String, String>,
    var image : Image,
    val market_data: MarketData?,
    val market_cap_rank : Int
)
data class Image(
    var thumb: String,
    var small: String,
    var large: String
)
data class MarketData(
    val current_price: Map<String, Double>,
    val price_change_percentage_24h: Double?,

    val total_volume : Map<String, Double>,
    val market_cap : Map<String, Double>,

    val atl : Map<String, Float>,
    val ath : Map<String, Float>,


)
