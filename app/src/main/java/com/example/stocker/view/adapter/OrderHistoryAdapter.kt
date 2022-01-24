package com.example.stocker.view.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.view.customviews.CustomTextView
import com.example.stocker.view.util.DisplayUtil
import com.google.android.material.textview.MaterialTextView
import java.util.regex.Pattern

const val AdminMode=1
const val CustomerMode=2

class OrderHistoryAdapter(val context: Context, val navController: NavController, private val mode:Int): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>(){


       private val diff = AsyncListDiffer(this, DiffCalc())
        private lateinit var recyclerView: RecyclerView
    private var smoothScroller: RecyclerView.SmoothScroller


    init {
        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView=recyclerView
    }

    fun setNewList(newData:List<OrderHistory>){

        diff.submitList(newData){

                recyclerView.post {
                    smoothScroller.targetPosition=0
                    recyclerView.layoutManager!!.startSmoothScroll(smoothScroller)
                }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.order_history_adapter_view,parent,false)

        val holder = ViewHolder(layout)
        holder.itemView.setOnClickListener {
            val data = diff.currentList
            when(mode){
                AdminMode->{
                    navController.navigate(R.id.action_adminOrderHistoryFragment_to_orderHistroyViewer,
                        bundleOf("data" to ("data" to data[holder.adapterPosition])))
                }
                CustomerMode->{
                    navController.navigate(R.id.action_order_History_fragment_to_orderHistroyViewer2,
                        bundleOf("data" to ("data" to data[holder.adapterPosition])))
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data =diff.currentList

        holder.orderId.text="O id: ${data[position].orderID}"
        holder.customerId.text = "C id: ${data[position].customerId}"
        holder.orderDate.text=data[position].dateOfPurchase.toString()
        holder.orderTotal.text = "₹${data[position].total}"
        
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

        val orderId: MaterialTextView = view.findViewById(R.id.order_history_id)
        val customerId:MaterialTextView = view.findViewById(R.id.customer_id)
        val orderTotal:MaterialTextView = view.findViewById(R.id.order_total)
        val orderDate:MaterialTextView = view.findViewById(R.id.order_date)



    }


}