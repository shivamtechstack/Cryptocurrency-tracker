package com.sycodes.cryptotrack.utility

import android.content.Context
import android.icu.util.Currency

class PreferencesHelper(context: Context) {

    private val sharedPreferencesForSorting = context.getSharedPreferences("sorting_preferences", Context.MODE_PRIVATE)

    fun saveSortingOption(sortOption: String) {
        sharedPreferencesForSorting.edit().
            putString("sorting_option", sortOption)
            .apply()

    }
    fun saveCurrencySelection(currency: String){
        sharedPreferencesForSorting.edit().putString("currency_selection",currency).apply()
    }

    fun getSortingOption(): String? {
        return sharedPreferencesForSorting
            .getString("sorting_option", "market_cap_desc")
    }

    fun getCurrencySorting(): String? {
        return sharedPreferencesForSorting
            .getString("currency_selection", "usd")

    }
}