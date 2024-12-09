package com.sycodes.cryptotrack

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.sycodes.cryptotrack.databinding.ActivityCoinDetailBinding
import com.sycodes.cryptotrack.instance.RetrofitInstance
import com.sycodes.cryptotrack.model.CoinDataById
import com.sycodes.cryptotrack.utility.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoinDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoinDetailBinding
    private lateinit var coinId : String
    private lateinit var prefrenceHelper: PreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCoinDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        coinId = intent.getStringExtra("coinId").toString()
        Log.d("CoinDetailActivity", "Received coinId: $coinId")

        prefrenceHelper = PreferencesHelper(this)
        val selectedCurrency = prefrenceHelper.getCurrencySorting()
        Log.d("CoinDetailActivity", "Selected currency: $selectedCurrency")

        fetchCoinData(selectedCurrency)
    }

    private fun fetchCoinData(selectedCurrency: String?) {

        RetrofitInstance.api.getCoinById(coinId).enqueue(object : Callback<CoinDataById>{
            override fun onResponse(call: Call<CoinDataById>, response: Response<CoinDataById>) {
                Log.d("API Response", "Code: ${response.code()}, Message: ${response.message()}")
                Log.d("API Response", "Raw response: ${response.raw()}")

                if (response.isSuccessful){
                    response.body().let { coin ->
                        Log.d("API Response", "Parsed coin data: $coin")
                        showDetails(coin,selectedCurrency)
                    }
                }else {
                    Log.e("API Error", "Unsuccessful response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CoinDataById>, t: Throwable) {
                Toast.makeText(this@CoinDetailActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                Log.e("API Error", "Error: ${t.message}")
            }

        })

    }
    private fun showDetails(coin: CoinDataById?, selectedCurrency: String?) {
        Log.d("CoinDetailActivity", "Showing details for coin: $coin")

        Glide.with(this)
            .load(coin?.image?.small)
            .circleCrop()
            .into(binding.cryptoImage)

        binding.cryptoSymbol.text = coin?.symbol

        binding.cryptoName.text = coin?.name

        binding.cryptoPrice.text = coin?.market_data?.current_price?.get(selectedCurrency).toString()

        binding.hourChange.text = "${coin?.market_data?.price_change_percentage_24h.toString()} %"
        if (coin?.market_data?.price_change_percentage_24h!! > 0) {
            binding.hourChange.setTextColor(getColor(R.color.Green))
        }else{
            binding.hourChange.setTextColor(getColor(R.color.Red))
        }

        binding.cryptoVolume.text = coin?.market_data?.total_volume?.get(selectedCurrency)?.toString()

        binding.cryptoMarketCap.text = coin?.market_data?.market_cap?.get(selectedCurrency)?.toString()

        binding.cryptoRank.text = "#${coin?.market_cap_rank.toString()}"

        binding.cryptoAllTimeHigh.text = coin?.market_data?.ath?.get(selectedCurrency)?.toString()

        binding.cryptoAllTimeLow.text = coin?.market_data?.atl?.get(selectedCurrency)?.toString()

        binding.cryptoDescription.text = coin?.description?.get("en")


    }
}