package com.sycodes.cryptotrack

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.sycodes.cryptotrack.databinding.ActivityCoinDetailBinding
import com.sycodes.cryptotrack.instance.RetrofitInstance
import com.sycodes.cryptotrack.model.CoinDataById
import com.sycodes.cryptotrack.model.HistoricalDataResponse
import com.sycodes.cryptotrack.utility.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

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

        prefrenceHelper = PreferencesHelper(this)
        val selectedCurrency = prefrenceHelper.getCurrencySorting()

        fetchCoinData(selectedCurrency)
        fetchHistoricalData(coinId, selectedCurrency)
    }

    private fun fetchHistoricalData(coinId: String, selectedCurrency: String?) {
        RetrofitInstance.api.getCoinMarketChart(coinId, selectedCurrency.toString(), "30")
            .enqueue(object : Callback<HistoricalDataResponse> {
                override fun onResponse(
                    call: Call<HistoricalDataResponse>,
                    response: Response<HistoricalDataResponse>
                ) {
                    if (response.isSuccessful){
                        response.body().let { data ->
                            if (data != null){
                                showHistoricalData(data.prices)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<HistoricalDataResponse>, t: Throwable) {
                    Log.e("HistoricalGraph", "Error: in graph")
                }
            })
    }

    private fun showHistoricalData(prices: List<List<Double>>) {
        val series = LineGraphSeries<DataPoint>()

        val thirtyDaysAgoTimestamp = prices.first()[0].toLong() * 1000
        val mostRecentTimestamp = prices.last()[0].toLong() * 1000

        for (priceData in prices) {
            val timestamp = priceData[0].toLong()*1000
            val price = priceData[1]
            val date = Date(timestamp)
            series.appendData(DataPoint(date, price), true, prices.size)
        }

        val graph = binding.graphView
        graph.addSeries(series)
        graph.title = "Historical Price Chart"

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        graph.gridLabelRenderer.numHorizontalLabels = 2

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(thirtyDaysAgoTimestamp.toDouble())
        graph.viewport.setMaxX(mostRecentTimestamp.toDouble())

        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
    }

    private fun fetchCoinData(selectedCurrency: String?) {

        RetrofitInstance.api.getCoinById(coinId).enqueue(object : Callback<CoinDataById>{
            override fun onResponse(call: Call<CoinDataById>, response: Response<CoinDataById>) {
                if (response.isSuccessful){
                    response.body().let { coin ->
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

    @SuppressLint("SetTextI18n")
    private fun showDetails(coin: CoinDataById?, selectedCurrency: String?) {

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

        binding.cryptoVolume.text = coin.market_data.total_volume.get(selectedCurrency)?.toString()

        binding.cryptoMarketCap.text = coin.market_data.market_cap.get(selectedCurrency)?.toString()

        binding.cryptoRank.text = "#${coin.market_cap_rank}"

        binding.cryptoAllTimeHigh.text = coin.market_data.ath.get(selectedCurrency)?.toString()

        binding.cryptoAllTimeLow.text = coin.market_data.atl.get(selectedCurrency)?.toString()

        binding.cryptoDescription.text = coin.description.get("en")

    }
}