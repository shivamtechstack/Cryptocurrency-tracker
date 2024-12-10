package com.sycodes.cryptotrack.utility

import android.content.Context

class CurrencySymbol(context: Context) {
    val selectedCurrency = PreferencesHelper(context).getCurrencySorting()

     fun getCurrencySymbol(): String{
         return when (selectedCurrency?.lowercase()) {
             "usd" -> "$"
             "eur" -> "€"
             "gbp" -> "£"
             "inr" -> "₹"
             "jpy" -> "¥"
             "btc" -> "₿"
             "eth" -> "Ξ"
             "bnb" -> "🅱"
             "usdt" -> "₮"
             "ada" -> "₳"
             "doge" -> "Ð"
             "aud" -> "A$"
             "cad" -> "C$"
             "rub" -> "₽"
             "cny" -> "¥"
             else -> "?"
         }
     }
}