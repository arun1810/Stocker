package com.example.stocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.view.customviews.CustomTextView
import com.example.stocker.view.fragments.util.Type
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView

class CustomerAdapter(val context: Context,private val selectedCustomer:MutableList<Customer>,val navController: NavController ,private val selectionListener: SelectionListener): RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    private var oneSelectionActive=false
    private var multipleSelectionActive=false
    private val diff = AsyncListDiffer(this, DiffCalc())
    private var changeType = Type.Nothing
    lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView=recyclerView
    }

    fun setNewList(newData:List<Customer>){
        diff.submitList(newData){
            if(changeType!=Type.Delete){
                recyclerView.scrollToPosition(0)
                changeType=Type.Nothing
            }
        }
    }
    fun selectionListChanged(type: Type){
        if(type== Type.Update) notifyDataSetChanged()
            changeSelectionState()
        changeType=type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout= LayoutInflater.from(context).inflate(R.layout.customer_adapter_layout,parent,false)

        val holder = ViewHolder(layout)

        holder.itemView.setOnLongClickListener{
            if(holder.state == ViewHolder.State.Unselected) {
                selectCustomer(holder)
                true
            }
            else{false}
        }

        holder.itemView.setOnClickListener {
            val data = diff.currentList
            if(oneSelectionActive || multipleSelectionActive){
                if(selectedCustomer.contains(data[holder.adapterPosition])){
                    unSelectCustomer(holder)
                }
                else{
                    selectCustomer(holder)
                }
            }
            else{
               navController.navigate(R.id.action_adminCustomerFragment_to_customerViewer, bundleOf("data" to ("data" to data[holder.adapterPosition])))
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


        holder.customerId.text = data[position].customerId
        holder.customerDob.text = data[position].dob.toString()
        holder.customerPh.text = "ph:${data[position].mobile_number}"
        holder.customerName.text = data[position].name
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
        holder.parent.setBackgroundColor(MaterialColors.getColor(context,R.attr.colorPrimary,context.getColor(R.color.darkPrimaryColor)))
        holder.state=ViewHolder.State.Selected
    }
    private fun changeToUnselectedState(holder:ViewHolder){

        holder.parent.setBackgroundColor(MaterialColors.getColor(context,android.R.attr.colorBackground,context.getColor(R.color.darkBackgroundColor)))
        holder.state = ViewHolder.State.Unselected
    }


    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        enum class State{Selected,Unselected}

        var state:State=State.Unselected
        val customerName:MaterialTextView = view.findViewById(R.id.customer_name)
        val customerId:MaterialTextView = view.findViewById(R.id.customer_id)
        val customerDob:MaterialTextView = view.findViewById(R.id.customer_dob)
        val customerPh:MaterialTextView = view.findViewById(R.id.customer_ph)
        val parent:ConstraintLayout = view.findViewById(R.id.parent)

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