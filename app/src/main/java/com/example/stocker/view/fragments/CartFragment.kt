package com.example.stocker.view.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.StockInCart
import com.example.stocker.view.adapter.CartAdapter
import com.example.stocker.viewmodel.CustomerViewModel
import com.example.stocker.viewmodel.helper.NetworkConnectivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch


class CartFragment : Fragment() {
    private lateinit var toolBar:MaterialToolbar
    private lateinit var recycler:RecyclerView
    private lateinit var buyBtn:MaterialButton
    private lateinit var totalPriceText:MaterialTextView
    private val model : CustomerViewModel by activityViewModels()
    private var isLoaded=false
    private lateinit var progress:CircularProgressIndicator
    private lateinit var buyLayout:ConstraintLayout
    private  var snackBar:Snackbar?=null
    private lateinit var navController:NavController
    private lateinit var stockInCart: StockInCart
    private lateinit var adapter:CartAdapter
    private lateinit var stockLoaderProgeress:CircularProgressIndicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController  = navHost.navController

        requireActivity().onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                snackBar?.dismiss()
                navController.popBackStack()
            }

        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.cartErrorStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_LONG).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        }

        stockLoaderProgeress = view.findViewById(R.id.stockLoaderProgress)
        stockLoaderProgeress.show()
        toolBar = view.findViewById(R.id.cart_toolbar)
        recycler  = view.findViewById(R.id.cart_recycler)
        buyBtn = view.findViewById(R.id.cart_buy_btn)
        totalPriceText = view.findViewById(R.id.cart_total_price_textview)
        buyLayout = view.findViewById(R.id.buy_layout)
        progress = view.findViewById(R.id.cart_progress)
        progress.hide()

        toolBar.title="Place Order"
        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()

        }
        val orderCountTextView = view.findViewById<TextView>(R.id.cart_order_count_textview)
        orderCountTextView.text = "${model.selectedArray.size} item(s) in your cart"

        adapter = CartAdapter(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launch {
            stockInCart = model.calcCart()
            adapter.setNewData(stockInCart)
            stockLoaderProgeress.hide()
            totalPriceText.text = "TOTAL: ₹${stockInCart.total}"
            isLoaded=true
        }


        recycler.layoutManager = LinearLayoutManager(context)
        val decor  =DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL)
        //decor.setDrawable(ContextCompat.getDrawable(context!!,R.drawable.divider)!!)
        decor.setDrawable(
            ColorDrawable(
            MaterialColors.getColor(requireContext(),R.attr.colorOnSurface,requireContext().getColor(R.color.darkAccent)))
        )
        recycler.addItemDecoration(decor)

        buyBtn.setOnClickListener {
            buy(view)

        }
    }

    private fun buy(view:View){
        if(NetworkConnectivity.isNetworkAvailable(requireContext())){
            if(isLoaded){

                buyBtn.visibility=View.INVISIBLE
                progress.show()


                lifecycleScope.launch {
                    if(model.placeOrder(stockInCart.total)){
                        progress.hide()
                        // buyLayout.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(),"Order Placed",Toast.LENGTH_LONG).show()
                        navController.popBackStack()



                    }
                    else{
                        progress.hide()
                        buyBtn.visibility=View.VISIBLE
                        snackBar = Snackbar.make(view,"Something went wrong. Try again later",Snackbar.LENGTH_LONG).apply {
                            anchorView = buyLayout
                            show()
                        }
                    }
                }
            }
        }
        else{
            snackBar = Snackbar.make(view,"No internet Connection",Snackbar.LENGTH_LONG).apply {
                setAction("retry") { buy(view) }
                anchorView = buyLayout
                show()
            }
        }
    }

}