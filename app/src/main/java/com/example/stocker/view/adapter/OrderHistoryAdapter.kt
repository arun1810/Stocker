package com.example.stocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.OrderHistory

class OrderHistoryAdapter(val context: Context, ): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

       private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<OrderHistory>){
        diff.submitList(newData)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.order_history_adapter_view,parent,false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList

        holder.orderId.text=data[position].orderID
        holder.customerId.text=data[position].customerId
        holder.orderDate.text=data[position].dateOfPurchase.toString()
        holder.orderTotal.text=data[position].total.toString()
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
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

        val orderId: TextView = view.findViewById(R.id.order_history_id)
        val customerId:TextView = view.findViewById(R.id.customer_id)
        val orderTotal:TextView = view.findViewById(R.id.order_total)
        val orderDate:TextView = view.findViewById(R.id.order_date)

    }
}