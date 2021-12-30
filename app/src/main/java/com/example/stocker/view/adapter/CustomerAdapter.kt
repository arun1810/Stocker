package com.example.stocker.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.view.customviews.ScrollableTextView
import com.example.stocker.view.fragments.util.Type
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class CustomerAdapter(val context: Context,private val selectedCustomer:MutableList<Customer>, private val selectionListener: SelectionListener): RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    private var oneSelectionActive=false
    private var multipleSelectionActive=false
    private val diff = AsyncListDiffer(this, DiffCalc())

    fun setNewList(newData:List<Customer>){
        diff.submitList(newData)
    }
    fun selectionListChanged(type: Type){
        if(type== Type.Update) notifyDataSetChanged()
            changeSelectionState()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.customer_adapter_layout,parent,false)

        val holder = ViewHolder(layout)

        holder.layout.setOnLongClickListener{
            val data = diff.currentList
            if(holder.state == ViewHolder.State.Unselected) {
                selectCustomer(holder)
                true
            }
            else{false}
        }

        holder.layout.setOnClickListener {
            val data = diff.currentList
            if(oneSelectionActive || multipleSelectionActive){
                if(selectedCustomer.contains(data[holder.adapterPosition])){
                    unSelectCustomer(holder)
                }
                else{
                    selectCustomer(holder)
                }
            }
        }
        return  holder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {





        val data = diff.currentList
        if(selectedCustomer.contains(data[position])){
            changeToSelectedState(holder)
        }
        else{
            changeToUnselectedState(holder)
        }


        holder.customerId.setText(data[position].customerId)
        holder.customerDob.text = data[position].dob.toString()
        holder.customerPh.text = "ph:${data[position].mobile_number}"
        holder.customerName.setText(data[position].name)
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
        val data = diff.currentList
        if (selectedCustomer.contains(data[holder.adapterPosition])) {
            changeToUnselectedState(holder)
            selectedCustomer.remove(data[holder.adapterPosition])
            changeSelectionState()

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
            if(oneSelectionActive || multipleSelectionActive) {
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
        val customerName:ScrollableTextView = view.findViewById(R.id.customer_name)
        val customerId:ScrollableTextView = view.findViewById(R.id.customer_id)
        val customerDob:MaterialTextView = view.findViewById(R.id.customer_dob)
        val customerPh:MaterialTextView = view.findViewById(R.id.customer_ph)
        val layout:ConstraintLayout = view.findViewById(R.id.parent)

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