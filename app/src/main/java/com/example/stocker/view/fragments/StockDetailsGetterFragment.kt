package com.example.stocker.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.example.stocker.view.fragments.util.Mode
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class StockDetailsGetterFragment : DialogFragment() {

    val invalidInput="InvalidInput"

    private fun Boolean.doIfTrue(lam:()->Unit){
        if(this){
            lam()
        }
    }

    private val model :AdminViewModel by activityViewModels()
    private lateinit var stockIdLayout:TextInputLayout
    private lateinit var stockIdEtx:TextInputEditText
    private lateinit var stockNameLayout:TextInputLayout
    private lateinit var stockNameEtx:TextInputEditText
    private lateinit var stockPriceLayout:TextInputLayout
    private lateinit var stockPriceEtx:TextInputEditText
    private lateinit var stockCountLayout:TextInputLayout
    private lateinit var stockCountEtx:TextInputEditText
    private lateinit var stockDiscountLayout:TextInputLayout
    private lateinit var stockDiscountEtx:TextInputEditText
    private lateinit var addBtn:MaterialButton
    private lateinit var cancelBtn:MaterialButton
    private var mode = Mode.Create

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mode = arguments?.get("mode") as Mode
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock_details_getter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stockIdLayout = view.findViewById(R.id.admin_stock_id_layout)
        stockIdEtx = view.findViewById(R.id.admin_stock_id_etx)
        stockNameLayout = view.findViewById(R.id.admin_stock_name_layout)
        stockNameEtx = view.findViewById(R.id.admin_stock_name_etx)
        stockPriceLayout = view.findViewById(R.id.admin_stock_price_layout)
        stockPriceEtx = view.findViewById(R.id.admin_stock_price_etx)
        stockCountLayout = view.findViewById(R.id.admin_stock_count_layout)
        stockCountEtx = view.findViewById(R.id.admin_stock_count_etx)
        stockDiscountLayout = view.findViewById(R.id.admin_stock_discount_layout)
        stockDiscountEtx = view.findViewById(R.id.admin_stock_discount_etx)
        addBtn = view.findViewById(R.id.admin_stock_add_btn)
        cancelBtn = view.findViewById(R.id.admin_stock_cancel_btn)


        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_INDEFINITE).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })

        dialog!!.setCancelable(false)
        val size = DisplayUtil.getDisplaySize(activity!!)
        val height = size.x
        val width = size.y
        dialog!!.window!!.setLayout(width, (height/1.5).toInt())

        stockIdEtx.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                stockIdLayout.error=null
            }

        })



        stockNameEtx.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                stockNameLayout.error=null
            }

        })

        stockPriceEtx.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isEmpty()) {
                    if (stockPriceLayout.error != null) stockPriceLayout.error = null
                } else {
                    try {
                        s.toString().toInt()
                        stockPriceLayout.error = null
                    } catch (e: NumberFormatException) {
                        stockPriceLayout.error = invalidInput
                    }
                }
            }

        })

        stockCountEtx.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isEmpty()) {
                    if (stockCountLayout.error != null) stockCountLayout.error = null
                } else {
                    try {
                        s.toString().toInt()
                        stockCountLayout.error = null
                    } catch (e: NumberFormatException) {
                        stockCountLayout.error = invalidInput
                    }
                }
            }

        })

        stockDiscountEtx.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isEmpty()) {
                    if (stockDiscountLayout.error != null) stockPriceLayout.error = null
                } else {
                    try {
                        s.toString().toInt()
                        stockDiscountLayout.error = null
                    } catch (e: NumberFormatException) {
                        stockDiscountLayout.error = invalidInput
                    }
                }
            }

        })




        cancelBtn.setOnClickListener {
            dialog!!.cancel()
        }

        addBtn.setOnClickListener {
            var canAdd=true
            stockIdEtx.text?.isEmpty()!!.doIfTrue {
                stockIdLayout.error="stock ID cannot be empty"
                canAdd=false
            }
            stockNameEtx.text?.isEmpty()!!.doIfTrue {
                stockNameLayout.error="stock Name cannot be empty"
                canAdd=false
            }

            if(stockPriceLayout.error!=null) {
                canAdd=false
            }
            else{
                stockPriceEtx.text!!.isEmpty().doIfTrue {
                    stockPriceLayout.error="stock price cannot be empty"
                    canAdd=false
                }
            }

            if(stockCountLayout.error!=null) {
                canAdd=false
            }
            else{
                stockCountEtx.text!!.isEmpty().doIfTrue {
                    stockCountLayout.error="stock Count cannot be empty"
                    canAdd=false
                }
            }

            if(stockDiscountLayout.error!=null) {
                canAdd=false
            }
            else{
                stockDiscountEtx.text!!.isEmpty().doIfTrue {
                    stockDiscountLayout.error="stock Discount cannot be empty"
                    canAdd=false
                }
            }



            if(canAdd){
                val stock = Stock(
                    stockID = stockIdEtx.text.toString(),
                    stockName = stockNameEtx.text.toString(),
                    price = stockPriceEtx.text.toString().toInt(),
                    count = stockCountEtx.text.toString().toInt(),
                    discount = stockDiscountEtx.text.toString().toInt()
                )
                when(mode){
                    Mode.Create -> {
                        model.addStock(stock)
                        model.join {
                            Toast.makeText(context!!, "Stock added", Toast.LENGTH_LONG).show()
                            dialog?.cancel()
                        }
                    }
                    Mode.Update->{
                        model.updateStock(model.selectedStocks[0],stock)
                        model.join {
                            Toast.makeText(context!!, "Stock updated", Toast.LENGTH_LONG).show()
                            dialog?.cancel()
                        }
                    }
                }


                model.join {
                    Toast.makeText(context!!,"Stock added", Toast.LENGTH_LONG).show()
                    dialog?.cancel()
                }
            }



        }


    }

    override fun onStart() {
        super.onStart()

        if(mode == Mode.Update){
            val updateStock = model.selectedStocks[0]

            updateStock.apply {
                stockIdEtx.setText(this.stockID)
                stockNameEtx.setText(this.stockName)
                stockCountEtx.setText(this.count.toString())
                stockDiscountEtx.setText(this.discount.toString())
                stockPriceEtx.setText(this.price.toString())
            }
            addBtn.text = "update"
        }
    }

}