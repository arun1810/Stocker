package com.example.stocker.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.google.android.material.textview.MaterialTextView

class CustomerAdapter(val context: Context,private val selectedCustomer:MutableList<Customer>, private val selectionListener: SelectionListener): RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    private var oneSelectionActive=false
    private var multipleSelectionActive=false
    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<Customer>){
        diff.submitList(newData)
    }
    fun selectionListChanged(){
            changeSelectionState()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.customer_adapter_layout,parent,false)
        return  ViewHolder(layout)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnLongClickListener{
            if(holder.state == ViewHolder.State.Unselected) {
                selectCustomer(holder)
                true
            }
            else{false}
        }

        holder.itemView.setOnClickListener {
            if(oneSelectionActive || multipleSelectionActive){
                if(selectedCustomer.contains(diff.currentList[holder.adapterPosition])){
                    unSelectCustomer(holder)
                }
                else{
                    selectCustomer(holder)
                }
            }
        }

        if(selectedCustomer.contains(diff.currentList[position])){
            changeToSelectedState(holder)
        }
        else{
            changeToUnselectedState(holder)
        }


        holder.customerId.text = diff.currentList[position].customerId
        holder.customerDob.text = diff.currentList[position].dob.toString()
        holder.customerPh.text = diff.currentList[position].mobile_number
        holder.customerName.text = diff.currentList[position].name
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    private fun selectCustomer(holder:ViewHolder){

            changeToSelectedState(holder)
            selectedCustomer.add(diff.currentList[holder.adapterPosition])
            changeSelectionState()
    }
    private fun unSelectCustomer(holder:ViewHolder) {
        if (selectedCustomer.contains(diff.currentList[holder.adapterPosition])) {
            changeToUnselectedState(holder)
            selectedCustomer.remove(diff.currentList[holder.adapterPosition])
            changeSelectionState()
            /*if (selectedCustomer.size == 0) {
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
        if(selectedCustomer.size>1){
           if(!multipleSelectionActive)
           {
               multipleSelectionActive=true
               oneSelectionActive=false
               selectionListener.onMultipleSelect()

           }
        }
        else if(selectedCustomer.size==1) {
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

    private fun changeToSelectedState(holder:ViewHolder){
        holder.itemView.setBackgroundColor(Color.BLUE)
        holder.state=ViewHolder.State.Selected
    }
    private fun changeToUnselectedState(holder:ViewHolder){
        holder.itemView.setBackgroundColor(Color.BLACK)
        holder.state = ViewHolder.State.Unselected
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        enum class State{Selected,Unselected}

        var state:State=State.Unselected
        val customerName:MaterialTextView = view.findViewById(R.id.customer_name)
        val customerId:MaterialTextView = view.findViewById(R.id.customer_id)
        val customerDob:MaterialTextView = view.findViewById(R.id.customer_dob)
        val customerPh:MaterialTextView = view.findViewById(R.id.customer_ph)
    }
    class DiffCalc: DiffUtil.ItemCallback<Customer>(){
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.customerId==newItem.customerId
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem==newItem
        }
    }
}