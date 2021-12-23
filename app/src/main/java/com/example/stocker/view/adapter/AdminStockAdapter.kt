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

class AdminStockAdapter(private val context: Context, private val cardHeight:Int,private val selectedStocks:MutableList<Stock>,private val selectionListener: SelectionListener): RecyclerView.Adapter<AdminStockAdapter.ViewHolder>() {


    private var oneSelectionActive = false
    private var multipleSelectionActive = false
    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<Stock>){
        diff.submitList(newData)
    }
    fun selectionListChanged(){
        changeSelectionState()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.admin_stock_recycler_layout,parent,false)
        val viewHolder = ViewHolder(layout)

        viewHolder.layout.layoutParams = viewHolder.layout.layoutParams.apply {
            this.height=cardHeight
        }
        
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList
        
        
        
        holder.stockName.text= data[position].stockName
        holder.stockPrice.text = data[position].price.toString()
        holder.stockCount.text = data[position].count.toString()
        holder.stockId.text=data[position].stockID

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
                if (oneSelectionActive) {
                    oneSelectionActive = false
                    multipleSelectionActive = false
                    selectionListener.selectionDisabled()
                }
            }
        }
    


    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        enum class State{Selected,Unselected}

        var state:State=State.Unselected
        val stockName: TextView = view.findViewById(R.id.stock_name)
        val stockId: TextView = view.findViewById(R.id.stock_id)
        val stockPrice: TextView = view.findViewById(R.id.stock_price)
        val stockCount: TextView = view.findViewById(R.id.stock_count)
        val layout:ConstraintLayout = view.findViewById(R.id.parent_layout)


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