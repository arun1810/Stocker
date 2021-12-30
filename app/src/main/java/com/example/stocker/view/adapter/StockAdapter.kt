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
import com.example.stocker.view.util.DisplayUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random

class StockAdapter( private val context:Context,private val selectedArray:HashMap<Stock,Int>,private val width:Int,private val viewHeight:Int,private val stockViewHeight:Int,private val btnLayoutHeight:Int,private val parent:RecyclerView):RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    private val diff = AsyncListDiffer(this, DiffCalc())
    private val imgs = arrayOf(R.mipmap.bike1_foreground,R.mipmap.bike2_foreground,R.mipmap.bike3_foreground,R.mipmap.bike4_foreground,R.mipmap.car1_foreground,R.mipmap.car2_foreground,R.mipmap.car3_foreground,R.mipmap.car4_foreground,R.mipmap.car5_foreground)



    fun setNewList(newData:List<Stock>){
        diff.submitList(newData)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.stock_recycler_layout,parent,false)
        val viewHolder = ViewHolder(layout,viewHeight,stockViewHeight ,width,imgs[Random.nextInt(imgs.size)])

        viewHolder.itemView.setOnClickListener {


            val position = viewHolder.adapterPosition
            val currentStock = diff.currentList[position]
            var count = selectedArray[currentStock] ?: 0
            //var count=10
            if(viewHolder.badge.visibility==View.INVISIBLE && currentStock.count>0){

                TransitionManager.beginDelayedTransition(this@StockAdapter.parent)
                selectedArray[currentStock] = ++count
                showBtnLayout(viewHolder,count)

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
                    TransitionManager.beginDelayedTransition(this@StockAdapter.parent)
                    hideBtnLayout(viewHolder)

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
        holder.stockPrice.text = "₹ ${data[position].price}"
        holder.stockId.text="id: ${data[position].stockID}"

        val currentStock = data[position]

        val count = selectedArray[currentStock] ?: 0
        if(count!=0){
            /*holder.badge.visibility=View.VISIBLE
            holder.badge.text=selectedArray[currentStock].toString()
            holder.btnLayout.visibility=View.VISIBLE

             */
            showBtnLayout(viewHolder = holder,count)
        }
        else{
           /* holder.badge.visibility=View.INVISIBLE
            holder.btnLayout.visibility=View.GONE
            */
            hideBtnLayout(viewHolder = holder)
        }

    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    private fun showBtnLayout(viewHolder: ViewHolder,count:Int){
        if(viewHolder.canShowBtnLayout){

            viewHolder.itemView.layoutParams = viewHolder.itemView.layoutParams.apply {
                this.width=this@StockAdapter.width
                this.height=(this@StockAdapter.viewHeight+btnLayoutHeight)
            }

        }
        viewHolder.badge.visibility=View.VISIBLE
        viewHolder.badge.text = count.toString()
        viewHolder.btnLayout.visibility=View.VISIBLE
        viewHolder.canShowBtnLayout=false
    }

    private fun hideBtnLayout(viewHolder:ViewHolder){
        if(! viewHolder.canShowBtnLayout){
            viewHolder.itemView.layoutParams = viewHolder.itemView.layoutParams.apply {
                this.width=this@StockAdapter.width
                this.height=(this@StockAdapter.viewHeight)
            }

        }
        viewHolder.btnLayout.visibility=View.GONE
        viewHolder.badge.visibility=View.INVISIBLE
        viewHolder.canShowBtnLayout=true
    }


    class ViewHolder(view: View, viewHeight: Int,stockHeight:Int,  width: Int, imgResource:Int):RecyclerView.ViewHolder(view){
         var canShowBtnLayout=true
        val parent:MaterialCardView = view.findViewById(R.id.parent)
        val stockName: TextView = view.findViewById(R.id.stock_name)
        val stockId: TextView = view.findViewById(R.id.stock_id)
        val stockPrice: TextView = view.findViewById(R.id.stock_price)
        val badge:TextView = view.findViewById(R.id.badge)
        val btnLayout:LinearLayoutCompat = view.findViewById(R.id.btn_layout)
        private val img :ShapeableImageView = view.findViewById(R.id.stock_img)
        val plusBtn:ImageView = view.findViewById(R.id.plus_btn)
        val minusBtn:ImageView = view.findViewById(R.id.minus_btn)
        private val stockLayout = view.findViewById<View>(R.id.stock_layout)

        init{
            img.setImageResource(imgResource)

            view.layoutParams = view.layoutParams.apply {
                this.width=width
                this.height=viewHeight
            }

            stockLayout.layoutParams = stockLayout.layoutParams.apply {
                this.height=stockHeight

            }

        }

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