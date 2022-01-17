package com.example.stocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.example.stocker.view.fragments.util.Type
import com.google.android.material.color.MaterialColors
import com.google.android.material.imageview.ShapeableImageView
import java.util.regex.Pattern
import kotlin.random.Random
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller


class AdminStockAdapter(private val context: Context,private val selectedStocks:MutableList<Stock>,private val width:Int,val navController:NavController,private val selectionListener: SelectionListener): RecyclerView.Adapter<AdminStockAdapter.ViewHolder>(){

    private var oneSelectionActive = false
    private var multipleSelectionActive = false
    private val diff = AsyncListDiffer(this, DiffCalc())

    private var smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
    private val images = arrayOf(
        R.mipmap.bike1_foreground,
        R.mipmap.bike2_foreground,
        R.mipmap.bike3_foreground,
        R.mipmap.bike4_foreground,
        R.mipmap.car1_foreground,
        R.mipmap.car2_foreground,
        R.mipmap.car3_foreground,
        R.mipmap.car4_foreground,
        R.mipmap.car5_foreground
    )
    private lateinit var recyclerView: RecyclerView
    private var changeType = Type.Nothing

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }


    fun setNewList(newData: List<Stock>) {

        diff.submitList(newData) {

            if ((changeType != Type.Delete)) {
                //recyclerView.scrollToPosition(0)
                    recyclerView.post {
                        smoothScroller.targetPosition=0
                        recyclerView.layoutManager!!.startSmoothScroll(smoothScroller)
                    }

                //recyclerView.layoutManager!!.smoothScrollToPosition(recyclerView,RecyclerView.State)
            }
            changeType = Type.Nothing

        }
    }

    fun selectionListChanged(type: Type) {
        if (type == Type.Update) {
            notifyDataSetChanged()
        }
        changeType = type
        changeSelectionState()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val data = diff.currentList

        val layout = LayoutInflater.from(context)
            .inflate(R.layout.admin_stock_recycler_card_layout, parent, false)

        val holder = ViewHolder(layout, width, images[Random.nextInt(images.size)])

        holder.itemView.setOnLongClickListener {
            if (holder.state == ViewHolder.State.Unselected) {
                selectStock(holder)
                true
            } else {
                false
            }
        }

        holder.itemView.setOnClickListener {
            //val data = filteredList
            val data = diff.currentList
            if (oneSelectionActive || multipleSelectionActive) {
                //if (selectedStocks.contains(diff.currentList[holder.adapterPosition])) {
                if (selectedStocks.contains(data[holder.adapterPosition])) {
                    unSelectStock(holder)
                } else {
                    selectStock(holder)
                }
            } else {
                navController.navigate(
                    R.id.action_adminStockFragment_to_stockViewer,
                    //bundleOf("data" to (holder.imgResource to diff.currentList[holder.adapterPosition])),
                    bundleOf("data" to (holder.imgResource to data[holder.adapterPosition])),

                    null,
                    FragmentNavigatorExtras(holder.itemView to "stock_viewer_img")
                )
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = diff.currentList
            //val data = filteredList



        holder.stockName.text = data[position].stockName
        holder.stockPrice.text = "₹ ${data[position].price}"
        holder.stockId.text = "id: ${data[position].stockID}"



       // if (selectedStocks.contains(diff.currentList[position])) {
        if (selectedStocks.contains(data[position])) {
            changeToSelectedState(holder)
        } else {
            changeToUnselectedState(holder)
        }


    }

    override fun getItemCount(): Int {
        return diff.currentList.size
        //return filteredList.size
    }

    private fun selectStock(holder: ViewHolder) {
        val data = diff.currentList
        //val data = filteredList

        changeToSelectedState(holder)
        selectedStocks.add(data[holder.adapterPosition])
        changeSelectionState()
    }

    private fun unSelectStock(holder: ViewHolder) {
        val data = diff.currentList
        //val data = filteredList
        if (selectedStocks.contains(data[holder.adapterPosition])) {
            changeToUnselectedState(holder)
            selectedStocks.remove(data[holder.adapterPosition])
            changeSelectionState()
        }
    }

    private fun changeToSelectedState(holder: ViewHolder) {
        //holder.itemView.setBackgroundColor()
        //(holder.itemView as MaterialCardView).strokeColor = context.getColor(R.color.darkPrimaryTextColor)
        holder.parent.setBackgroundColor(
            MaterialColors.getColor(
                context,
                R.attr.colorPrimary,
                context.getColor(R.color.darkPrimaryColor)
            )
        )
        holder.state = ViewHolder.State.Selected
    }

    private fun changeToUnselectedState(holder: ViewHolder) {
        //holder.itemView.setBackgroundColor(context.getColor(R.color.darkBackgroundColor))
        //(holder.itemView as MaterialCardView).strokeColor = context.getColor(R.color.darkPrimaryLightColor)
        holder.parent.setBackgroundColor(
            MaterialColors.getColor(
                context,
                android.R.attr.colorBackground,
                context.getColor(R.color.darkBackgroundColor)
            )
        )
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


    class ViewHolder(view: View, width: Int, val imgResource: Int) : RecyclerView.ViewHolder(view) {
        enum class State { Selected, Unselected }


        var state: State = State.Unselected
        val stockName: TextView = view.findViewById(R.id.stock_name)
        val stockId: TextView = view.findViewById(R.id.stock_id)
        val stockPrice: TextView = view.findViewById(R.id.stock_price)
        val layout: ConstraintLayout = view.findViewById(R.id.parent_layout)
        private val img: ShapeableImageView = view.findViewById(R.id.stock_img)
        val parent: ConstraintLayout = view.findViewById(R.id.parent_layout)

        init {
            Glide.with(view).load(imgResource).into(img)
            //img.setImageResource(imgResource)

            view.layoutParams = view.layoutParams.apply {
                this.width = width
            }
        }


    }


    class DiffCalc : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.stockID == newItem.stockID
        }

        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {

            return oldItem == newItem
        }

    }
}