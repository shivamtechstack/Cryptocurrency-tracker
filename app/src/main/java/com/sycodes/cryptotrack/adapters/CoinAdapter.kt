package com.sycodes.cryptotrack.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sycodes.cryptotrack.CoinDetailActivity
import com.sycodes.cryptotrack.MainActivity
import com.sycodes.cryptotrack.R
import com.sycodes.cryptotrack.model.CoinDataHomePage

class CoinAdapter(private val context: Context, private var coinList: MutableList<CoinDataHomePage>): RecyclerView.Adapter<CoinAdapter.CoinViewHolder>() {

    class CoinViewHolder(view: View) : RecyclerView.ViewHolder(view){

        var coinImage = view.findViewById<ImageView>(R.id.crypto_image_AdapterView)!!
        var coinName = view.findViewById<TextView>(R.id.crypto_Name_AdapterView)!!
        var coinPrice = view.findViewById<TextView>(R.id.crypto_price_AdapterView)!!
        var coinChange = view.findViewById<TextView>(R.id.crypto_change_AdapterView)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.cryptoadapterview, parent, false)
        return CoinViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return coinList.size
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = coinList[position]

        holder.coinName.text = coin.name + " ( ${coin.symbol} )"

        holder.coinPrice.text = "${coin.current_price}"

        holder.coinChange.text = "${coin.price_change_percentage_24h}%"

        if (coin.price_change_percentage_24h>0){
            holder.coinChange.setTextColor(holder.coinChange.context.getColor(R.color.Green))
        }else{
            holder.coinChange.setTextColor(holder.coinChange.context.getColor(R.color.Red))
        }

        Glide.with(holder.coinImage.context)
            .load(coin.image)
            .circleCrop()
            .placeholder(R.drawable.star_24)
            .into(holder.coinImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CoinDetailActivity::class.java)
            intent.putExtra("coinId",coin.id)
            holder.itemView.context.startActivity(intent)
        }
    }
}