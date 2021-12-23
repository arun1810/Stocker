package com.example.stocker.view.adapter

import android.content.Context
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.google.android.material.card.MaterialCardView

class StockAdapter( private val context:Context,private val cardHeight:Int,private val selectedArray:HashMap<Stock,Int>):RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<Stock>){
        diff.submitList(newData)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val layout= LayoutInflater.from(context).inflate(R.layout.stock_recycler_layout,parent,false)
        val viewHolder = ViewHolder(layout)

        viewHolder.parentLayout.layoutParams = viewHolder.parentLayout.layoutParams.apply {
            this.height=cardHeight
        }

        viewHolder.parentLayout.setOnClickListener {
            val position = viewHolder.adapterPosition
            val currentStock = diff.currentList[position]
            var count = selectedArray[currentStock] ?: 0
            //var count=10
            if(viewHolder.badge.visibility==View.INVISIBLE && currentStock.count>0){
                selectedArray[currentStock] = ++count
                viewHolder.badge.visibility=View.VISIBLE
                viewHolder.badge.text = count.toString()

                TransitionManager.beginDelayedTransition(viewHolder.parentLayout)
                viewHolder.btnLayout.visibility=View.VISIBLE
            }
            else if(currentStock.count==0){
                Toast.makeText(context,"Out of Stock!",Toast.LENGTH_LONG).show()
            }
        }

        viewHolder.plusBtn.setOnClickListener {
            val position = viewHolder.adapterPosition
            val currentStock = diff.currentList[position]
            var count = selectedArray[currentStock] ?: 0

            if(count< diff.currentList[position].count) {
                selectedArray[currentStock] = ++count
                viewHolder.badge.text = count.toString()
            }

        }

        viewHolder.minusBtn.setOnClickListener {
            val position = viewHolder.adapterPosition
            val currentStock = diff.currentList[position]
            var count = selectedArray[currentStock] ?: 0

            when {
                count==1 -> {
                    viewHolder.btnLayout.visibility=View.GONE
                    viewHolder.badge.visibility=View.INVISIBLE
                    selectedArray[currentStock] = --count
                    selectedArray.remove(currentStock)
                }
                count>1-> {
                    selectedArray[currentStock] = --count
                    viewHolder.badge.text=count.toString()
                }

            }

        }


        return viewHolder

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList
        holder.stockName.text= data[position].stockName
        holder.stockPrice.text = data[position].price.toString()
        holder.stockCount.text = data[position].count.toString()
        holder.stockId.text=data[position].stockID

        val currentStock = data[position]


        val count = selectedArray[currentStock] ?: 0
        if(count!=0){
            holder.badge.visibility=View.VISIBLE
            holder.badge.text=selectedArray[currentStock].toString()
            holder.btnLayout.visibility=View.VISIBLE
        }
        else{
            holder.badge.visibility=View.INVISIBLE
            holder.btnLayout.visibility=View.GONE
        }

    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }


    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val stockName: TextView = view.findViewById(R.id.stock_name)
        val stockId: TextView = view.findViewById(R.id.stock_id)
        val stockPrice: TextView = view.findViewById(R.id.stock_price)
        val stockCount: TextView = view.findViewById(R.id.stock_count)

        val stockLayout = view.findViewById<MaterialCardView>(R.id.stock_layout)
        val badge:TextView = view.findViewById(R.id.badge)
        val parentLayout:ConstraintLayout = view.findViewById(R.id.parent_layout)
        val btnLayout:LinearLayoutCompat = view.findViewById(R.id.btn_layout)
        val plusBtn:ImageView = view.findViewById(R.id.plus_btn)
        val minusBtn:ImageView = view.findViewById(R.id.minus_btn)

    }


     class DiffCalc:DiffUtil.ItemCallback<Stock>(){
         override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
             return oldItem.stockID==newItem.stockID
         }

         override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
             return oldItem==newItem
         }

     }

}