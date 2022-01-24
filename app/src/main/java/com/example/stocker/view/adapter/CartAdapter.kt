package com.example.stocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.example.stocker.pojo.StockInCart
import com.example.stocker.view.customviews.CustomTextView
import com.google.android.material.textview.MaterialTextView

class CartAdapter(private val context:Context):RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewData(newData:StockInCart){
        diff.submitList(mutableListOf(newData))
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val layout= LayoutInflater.from(context).inflate(R.layout.cart_recycler_view,parent,false)
         return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList[0]
        holder.stockName.text = data.stocksNames[position]
        holder.stockCount.text = "x${data.counts[position]}"
        holder.stockId.text = "Id: ${data.stockIds[position]}"
        holder.stockTotalPrice.text = "₹${data.price[position]}"

        /*
        if(data.price[position]==""){
            holder.stockTotalPrice.visibility = View.INVISIBLE
        }
        else{

            holder.stockTotalPrice.text = "₹${data.price[position]}"
        }

         */

    }

    override fun getItemCount(): Int {
        return if(diff.currentList.size==0) 0 else{diff.currentList[0].stockIds.size}
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val stockName:MaterialTextView = view.findViewById(R.id.cart_stock_name)
        val stockCount:MaterialTextView = view.findViewById(R.id.cart_stock_count)
        val stockTotalPrice:MaterialTextView = view.findViewById(R.id.cart_stock_total_price)
        val stockId:MaterialTextView = view.findViewById(R.id.cart_stock_id)
        }



        class DiffCalc : DiffUtil.ItemCallback<StockInCart>() {
        override fun areItemsTheSame(oldItem: StockInCart, newItem: StockInCart): Boolean {
            return oldItem.stockIds.contentEquals(newItem.stockIds)
        }

        override fun areContentsTheSame(oldItem: StockInCart, newItem: StockInCart): Boolean {

            return oldItem == newItem
        }

    }


}