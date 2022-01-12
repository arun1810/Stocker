package com.example.stocker.view.fragments

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.pojo.StockInCart
import com.example.stocker.view.adapter.CartAdapter
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.CustomerViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OrderHistroyViewer : DialogFragment() {
    lateinit var orderHistory:OrderHistory

    private lateinit var toolBar: MaterialToolbar
    private lateinit var recycler: RecyclerView
    private lateinit var buyBtn: MaterialButton
    private lateinit var totalPriceText: MaterialTextView
    private val model:AdminViewModel by activityViewModels()
    private val stockNames= mutableListOf<String>()
    private lateinit var progress:CircularProgressIndicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL,R.style.AppTheme_FullScreenDialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        orderHistory = (arguments?.get("data") as Pair<*,*>).second as OrderHistory

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderCountTextView = view.findViewById<TextView>(R.id.cart_order_count_textview)
        orderCountTextView.text = "${orderHistory.stockIds.size} item(s) in your cart"

        progress = view.findViewById(R.id.cart_progress)
        progress.hide()
        progress.visibility=View.GONE

        toolBar = view.findViewById(R.id.cart_toolbar)
        toolBar.title = "Order History"
        toolBar.setNavigationIcon(R.drawable.ic_baseline_close_24)

        toolBar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        recycler = view.findViewById(R.id.cart_recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        val decor  =DividerItemDecoration(context!!,DividerItemDecoration.VERTICAL)
        //decor.setDrawable(ContextCompat.getDrawable(context!!,R.drawable.divider)!!)
        decor.setDrawable(ColorDrawable(
            MaterialColors.getColor(context!!,R.attr.colorOnBackground,context!!.getColor(R.color.darkOnBackgroundColor))))
        recycler.addItemDecoration(decor)
        //recycler.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        prepareAdapter(orderHistory)



        buyBtn = view.findViewById(R.id.cart_buy_btn)
        buyBtn.visibility=View.GONE

        totalPriceText = view.findViewById(R.id.cart_total_price_textview)
        totalPriceText.text="TOTAL: ₹${orderHistory.total}"


    }

    private fun prepareAdapter(orderHistory: OrderHistory):Array<String>{
        lifecycleScope.launch(Dispatchers.IO){
         for(id in orderHistory.stockIds){


                val stock=model.getStock(id).await()
                if(stock!=null){
                    stockNames.add(stock.stockName)
                }
                else{
                    stockNames.add("This item doesn't exist now")
                }

            }
            val stockInCart = StockInCart(counts = orderHistory.counts, stockIds = orderHistory.stockIds, total = orderHistory.total,price=Array(orderHistory.stockIds.size){""}, stocksNames = stockNames.toTypedArray())
            lifecycleScope.launch(Dispatchers.Main){recycler.adapter=CartAdapter(context!!,stockInCart)}
        }

        return stockNames.toTypedArray()
    }




}