package com.example.stocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.StockInCart
import com.google.android.material.textview.MaterialTextView

class CartAdapter(private val context:Context,private val data:StockInCart):RecyclerView.Adapter<CartAdapter.ViewHolder>() {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val layout= LayoutInflater.from(context).inflate(R.layout.cart_recycler_view,parent,false)
         return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stockName.text = data.stocksNames[position]
        holder.stockCount.text = "x${data.counts[position].toString()}"
        holder.stockId.text = data.stockIds[position]
        holder.stockTotalPrice.text = data.price[position]

    }

    override fun getItemCount(): Int {
        return data.stockIds.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val stockName:MaterialTextView = view.findViewById(R.id.cart_stock_name)
        val stockCount:MaterialTextView = view.findViewById(R.id.cart_stock_count)
        val stockTotalPrice:MaterialTextView = view.findViewById(R.id.cart_stock_total_price)
        val stockId:MaterialTextView = view.findViewById(R.id.cart_stock_id)

    }


}