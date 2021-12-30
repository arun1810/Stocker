package com.example.stocker.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.example.stocker.view.fragments.util.Type
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random

class AdminStockAdapter(private val context: Context,private val selectedStocks:MutableList<Stock>,private val width:Int,private val height:Int,private val selectionListener: SelectionListener): RecyclerView.Adapter<AdminStockAdapter.ViewHolder>() {


    private var oneSelectionActive = false
    private var multipleSelectionActive = false
    private val diff = AsyncListDiffer(this, DiffCalc())
    private val imgs = arrayOf(R.mipmap.bike1_foreground,R.mipmap.bike2_foreground,R.mipmap.bike3_foreground,R.mipmap.bike4_foreground,R.mipmap.car1_foreground,R.mipmap.car2_foreground,R.mipmap.car3_foreground,R.mipmap.car4_foreground,R.mipmap.car5_foreground)



    fun setNewList(newData:List<Stock>){
        diff.submitList(newData)
    }
    fun selectionListChanged(type: Type){
       if(type==Type.Update)notifyDataSetChanged()
        changeSelectionState()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.admin_stock_recycler_card_layout, parent, false)


        return ViewHolder(layout, width, height,imgs[Random.nextInt(imgs.size)])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList
        
        
        
        holder.stockName.text= data[position].stockName
        holder.stockPrice.text = "₹ ${data[position].price}"
        holder.stockId.text="id: ${data[position].stockID}"

        holder.itemView.setOnLongClickListener{
            if(holder.state == ViewHolder.State.Unselected) {
                selectStock(holder)
                true
            }
            else{false}
        }

        holder.itemView.setOnClickListener {
            if(oneSelectionActive || multipleSelectionActive){
                if(selectedStocks.contains(diff.currentList[holder.adapterPosition])){
                    unSelectStock(holder)
                }
                else{
                    selectStock(holder)
                }
            }
        }

        if(selectedStocks.contains(diff.currentList[position])){
            changeToSelectedState(holder)
        }
        else{
            changeToUnselectedState(holder)
        }


    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    private fun selectStock(holder: ViewHolder){

        changeToSelectedState(holder)
        selectedStocks.add(diff.currentList[holder.adapterPosition])
        changeSelectionState()
    }
    private fun unSelectStock(holder:ViewHolder) {
        if (selectedStocks.contains(diff.currentList[holder.adapterPosition])) {
            changeToUnselectedState(holder)
            selectedStocks.remove(diff.currentList[holder.adapterPosition])
            changeSelectionState()
        }
    }
    private fun changeToSelectedState(holder: ViewHolder){
        holder.itemView.setBackgroundColor(Color.BLUE)
        holder.state= ViewHolder.State.Selected
    }
    private fun changeToUnselectedState(holder: ViewHolder){
        holder.itemView.setBackgroundColor(Color.BLACK)
        holder.state = ViewHolder.State.Unselected
    }

        private fun changeSelectionState() {

            if (selectedStocks.size > 1) {
                if (!multipleSelectionActive) {
                    multipleSelectionActive = true
                    oneSelectionActive = false
                    selectionListener.onMultipleSelect()

                }
            } else if (selectedStocks.size == 1) {
                if (!oneSelectionActive) {


                    oneSelectionActive = true
                    multipleSelectionActive = false
                    selectionListener.onOneSelect()
                }
            } else {
                if (oneSelectionActive || multipleSelectionActive) {
                    oneSelectionActive = false
                    multipleSelectionActive = false
                    selectionListener.selectionDisabled()
                }
            }
        }
    


    class ViewHolder(view: View,width:Int,height:Int,imgResource:Int):RecyclerView.ViewHolder(view){
        enum class State{Selected,Unselected}



        var state:State=State.Unselected
        val stockName: TextView = view.findViewById(R.id.stock_name)
        val stockId: TextView = view.findViewById(R.id.stock_id)
        val stockPrice: TextView = view.findViewById(R.id.stock_price)
        val layout:ConstraintLayout = view.findViewById(R.id.parent_layout)
        private val img:ShapeableImageView = view.findViewById(R.id.stock_img)


        init {
            img.setImageResource(imgResource)

            view.layoutParams = view.layoutParams.apply {
                this.width=width
                this.height=height
            }
        }


    }

   

    class DiffCalc: DiffUtil.ItemCallback<Stock>(){
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.stockID==newItem.stockID
        }

        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem==newItem
        }

    }

}