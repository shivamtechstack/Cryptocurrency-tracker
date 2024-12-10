package com.sycodes.cryptotrack.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.sycodes.cryptotrack.R
import com.sycodes.cryptotrack.adapters.CoinAdapter
import com.sycodes.cryptotrack.databinding.FragmentCryptoBinding
import com.sycodes.cryptotrack.instance.RetrofitInstance
import com.sycodes.cryptotrack.model.CoinDataHomePage
import com.sycodes.cryptotrack.utility.CurrencySymbol
import com.sycodes.cryptotrack.utility.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CryptoFragment : Fragment() {
    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceHelper: PreferencesHelper
    private var sortOption: String? = null
    private var currencySelection: String? = null
    private lateinit var coinAdapter: CoinAdapter
    private var coinList = mutableListOf<CoinDataHomePage>()
    private lateinit var currencySymbol: CurrencySymbol

    private var isLoading = false
    private var currentPage = 1
    private var pageSize = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferencesHelper(requireContext())
        currencySymbol = CurrencySymbol(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        val symbol = currencySymbol.getCurrencySymbol()
        initializeSpinners()
        val recyclerView = binding.cryptoRecyclerview
        coinAdapter = CoinAdapter(requireContext(),coinList, symbol)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = coinAdapter

        return binding.root
    }

    private fun initializeSpinners() {

        sortOption = preferenceHelper.getSortingOption()
        currencySelection = preferenceHelper.getCurrencySorting()

        val sortOptions = resources.getStringArray(R.array.sorting_options)
        val sortAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.sortBySpinner.adapter = sortAdapter
        binding.sortBySpinner.setSelection(sortOptions.indexOf(sortOption))

        val currencyOptions = resources.getStringArray(R.array.currencies_options)
        val currencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyOptions)
        currencyAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.CurrencySpinner.adapter = currencyAdapter
        binding.CurrencySpinner.setSelection(currencyOptions.indexOf(currencySelection))

        binding.sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortOption = parent?.getItemAtPosition(position).toString()
                preferenceHelper.saveSortingOption(sortOption!!)
                resetAndFetchCoin()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.CurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currencySelection = parent?.getItemAtPosition(position).toString()
                    preferenceHelper.saveCurrencySelection(currencySelection!!)
                    resetAndFetchCoin()

                    Log.e("API Error", "Invalid currency selection: $currencySelection")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fetchCoins()
    }
    private fun resetAndFetchCoin(){
        coinList.clear()
        coinAdapter.notifyDataSetChanged()
        currentPage = 1
        fetchCoins()
    }

    private fun fetchCoins() {
        isLoading = true

        RetrofitInstance.api.getCoinMarket(
            vsCurrency = currencySelection.toString(),
            order = sortOption.toString(),
            perPage = pageSize,
            page = currentPage
        ).enqueue(object : Callback<List<CoinDataHomePage>> {
            override fun onResponse(
                call: Call<List<CoinDataHomePage>>,
                response: Response<List<CoinDataHomePage>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        coinList.addAll(it)
                        coinAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("API Error", "Error: ${response.code()}, Message: ${response.message()}")
                    Log.e("API Error", "Response Body: ${response.errorBody()?.string()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<CoinDataHomePage>>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
                isLoading = false
            }
        })
    }
}
