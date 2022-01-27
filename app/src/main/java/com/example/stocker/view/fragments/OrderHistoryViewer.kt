package com.example.stocker.view.fragments

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.StockInCart
import com.example.stocker.view.adapter.CartAdapter
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderHistoryViewer : DialogFragment() {
    lateinit var orderHistory:OrderHistory

    private lateinit var toolBar: MaterialToolbar
    private lateinit var recycler: RecyclerView
    private lateinit var buyBtn: MaterialButton
    private lateinit var totalPriceText: MaterialTextView
    private val model:AdminViewModel by activityViewModels()
    private val stockNames= mutableListOf<String>()
    private lateinit var progress:CircularProgressIndicator
    private lateinit var stockLoaderProgress:CircularProgressIndicator
    private lateinit var adapter:CartAdapter


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

        adapter = CartAdapter(requireContext())
        // progress for buy which is not used in this fragment
        progress = view.findViewById(R.id.cart_progress)
        progress.hide()
        progress.visibility=View.GONE

        stockLoaderProgress = view.findViewById(R.id.stockLoaderProgress)
        stockLoaderProgress.bringToFront()
        stockLoaderProgress.show()



        toolBar = view.findViewById(R.id.cart_toolbar)
        toolBar.title  = "Cart"
        toolBar.setNavigationIcon(R.drawable.ic_baseline_close_24)

        toolBar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        recycler = view.findViewById(R.id.cart_recycler)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        val decor  =DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL)
        //decor.setDrawable(ContextCompat.getDrawable(context!!,R.drawable.divider)!!)
        decor.setDrawable(ColorDrawable(
            MaterialColors.getColor(requireContext(),R.attr.colorOnSurface,requireContext().getColor(R.color.darkAccent))))
        recycler.addItemDecoration(decor)
        //recycler.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        prepareAdapter(orderHistory)



        buyBtn = view.findViewById(R.id.cart_buy_btn)
        buyBtn.visibility=View.GONE

        totalPriceText = view.findViewById(R.id.cart_total_price_textview)
        totalPriceText.text="TOTAL: ₹${orderHistory.total}"


    }

    private fun prepareAdapter(orderHistory: OrderHistory):Array<String>{

            val stockInCart = StockInCart(
                counts = orderHistory.counts, stockIds = orderHistory.stockIds, total = orderHistory.total,
                price =orderHistory.stockPrices, stocksNames = orderHistory.stockNames)
            lifecycleScope.launch(Dispatchers.Main){

                stockLoaderProgress.hide()
                adapter.setNewData(stockInCart)

            }


        return stockNames.toTypedArray()
    }




}