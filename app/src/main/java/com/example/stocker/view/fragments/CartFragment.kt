package com.example.stocker.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.view.adapter.CartAdapter
import com.example.stocker.viewmodel.CustomerViewModel
import com.example.stocker.viewmodel.helper.NetworkConnectivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
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
    private var isloaded=false
    private lateinit var progress:CircularProgressIndicator
    private lateinit var buyLayout:ConstraintLayout
    private  var snackBar:Snackbar?=null
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController  = navHost.navController

        activity!!.onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){
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

        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_LONG).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })

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
            activity!!.onBackPressed()

        }
        val orderCountTextView = view.findViewById<TextView>(R.id.cart_order_count_textview)
        orderCountTextView.text = "${model.selectedArray.size} items in your cart"


        lifecycleScope.launch {
            val data = model.calcCart()
            recycler.adapter = CartAdapter(context!!,data)
            totalPriceText.text = "TOTAL:${data.total}"
            isloaded=true
        }


        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(DividerItemDecoration(context!!,DividerItemDecoration.VERTICAL))

        buyBtn.setOnClickListener {
            buy(view)

        }
    }

    private fun buy(view:View){
        if(NetworkConnectivity.isNetworkAvailable(context!!)){
            if(isloaded){

                buyBtn.visibility=View.INVISIBLE
                progress.show()


                lifecycleScope.launch {
                    if(model.placeOrder()){
                        progress.hide()
                        // buyLayout.visibility=View.INVISIBLE
                        snackBar = Snackbar.make(view,"Order Placed",Snackbar.LENGTH_INDEFINITE).setAction(
                            "Hurray"
                        ) {
                            activity?.onBackPressed()
                        }.setAnchorView(buyLayout)

                        snackBar!!.show()
                    }
                    else{
                        progress.hide()
                        snackBar = Snackbar.make(view,"Something went wrong",Snackbar.LENGTH_INDEFINITE).setAction(
                            "Close"
                        ) {
                            activity?.onBackPressed()
                        }.setAnchorView(buyLayout)

                        snackBar!!.show()
                    }
                }
            }
        }
        else{
            snackBar = Snackbar.make(view,"No internet Connection",Snackbar.LENGTH_INDEFINITE).setAction(
                "retry"
            ) {
                buy(view)
            }.setAnchorView(buyLayout)

            snackBar!!.show()
        }
    }

}