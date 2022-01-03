package com.example.stocker

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.view.adapter.SelectionListener

class AdminOrderHistoryAdapter(val context: Context, private val selectedOrder:MutableList<OrderHistory>, private val selectionListener: SelectionListener):RecyclerView.Adapter<AdminOrderHistoryAdapter.ViewHolder>() {

    private var oneSelectionActive = false
    private var multipleSelectionActive=false

    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<OrderHistory>){
        diff.submitList(newData)
    }
    fun selectionListChanged(){
        changeSelectionState()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.order_history_adapter_view,parent,false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList

        holder.itemView.setOnLongClickListener{
            if(holder.state == ViewHolder.State.Unselected) {
                selectCustomer(holder)
                true
            }
            else{false}
        }

        holder.itemView.setOnClickListener {
            if(oneSelectionActive || multipleSelectionActive){
                if(selectedOrder.contains(diff.currentList[holder.adapterPosition])){
                    unSelectCustomer(holder)
                }
                else{
                    selectCustomer(holder)
                }
            }
        }

        if(selectedOrder.contains(diff.currentList[position])){
            changeToSelectedState(holder)
        }
        else{
            changeToUnselectedState(holder)
        }

        holder.orderId.text=data[position].orderID
        holder.customerId.text=data[position].customerId
        holder.orderDate.text=data[position].dateOfPurchase.toString()
        holder.orderTotal.text=data[position].total.toString()
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    private fun selectCustomer(holder: ViewHolder){

        changeToSelectedState(holder)
        selectedOrder.add(diff.currentList[holder.adapterPosition])
        changeSelectionState()
    }
    private fun unSelectCustomer(holder: ViewHolder) {
        if (selectedOrder.contains(diff.currentList[holder.adapterPosition])) {
            changeToUnselectedState(holder)
            selectedOrder.remove(diff.currentList[holder.adapterPosition])
            changeSelectionState()
            /*if (selectedOrder.size == 0) {
                oneSelectionActive = false
                selectionListener.selectionDisabled()
            }
            else {
                select()
            }
             */
        }
    }

    private fun changeSelectionState(){
        if(selectedOrder.size>1){
            if(!multipleSelectionActive)
            {
                multipleSelectionActive=true
                oneSelectionActive=false
                selectionListener.onMultipleSelect()

            }
        }
        else if(selectedOrder.size==1) {
            if(!oneSelectionActive) {


                oneSelectionActive = true
                multipleSelectionActive = false
                selectionListener.onOneSelect()
            }
        }
        else{
            if(oneSelectionActive) {
                oneSelectionActive = false
                multipleSelectionActive = false
                selectionListener.selectionDisabled()
            }
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

    class DiffCalc: DiffUtil.ItemCallback<OrderHistory>(){
        override fun areItemsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
            return oldItem.orderID==newItem.orderID
        }
        override fun areContentsTheSame(oldItem: OrderHistory, newItem: OrderHistory): Boolean {
            return oldItem==newItem
        }
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        enum class State{Selected,Unselected}

        var state: State = State.Unselected
        val orderId: TextView = view.findViewById(R.id.order_history_id)
        val customerId:TextView = view.findViewById(R.id.customer_id)
        val orderTotal:TextView = view.findViewById(R.id.order_total)
        val orderDate:TextView = view.findViewById(R.id.order_date)

    }
}