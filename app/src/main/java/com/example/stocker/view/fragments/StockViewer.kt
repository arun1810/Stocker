package com.example.stocker.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.stocker.R
import com.example.stocker.pojo.Stock
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import kotlin.math.roundToInt

class StockViewer : DialogFragment(){

    lateinit var data:Pair<*,*>
    lateinit var toolbar:MaterialToolbar
    private lateinit var stockImage: ShapeableImageView
    lateinit var  stockName: MaterialTextView
    lateinit var stockId:MaterialTextView
    lateinit var stockCount:MaterialTextView
    private lateinit var stockMrpPrice:MaterialTextView
   lateinit var stockSpecialPrice:MaterialTextView
    private lateinit var youSaveText:MaterialTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppTheme_FullScreenDialog)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
         data = arguments?.get("data") as Pair<*, *>
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_stock_viewer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







        stockImage= view.findViewById(R.id.stock_img)
         stockName = view.findViewById(R.id.stock_name)
        stockId = view.findViewById(R.id.stock_id)
        stockCount=view.findViewById(R.id.stock_in_stock_textview)
        stockMrpPrice=view.findViewById(R.id.mrp_textview)
        stockSpecialPrice=view.findViewById(R.id.special_price_textview)
        youSaveText=view.findViewById(R.id.you_save_textview)
        toolbar= view.findViewById(R.id.stock_viewer_toolbar)

    }

    override fun onStart() {
        super.onStart()
        val imgRes = data.first as Int
        val stock = data.second as Stock

        dialog!!.setCancelable(false)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        toolbar.title="Stock"
        toolbar.setNavigationOnClickListener {

            dialog!!.cancel()
        }


        stockImage.setImageResource(imgRes)
        stockName.text = stock.stockName
        stockId.text="id: ${stock.stockID}"
        stock.count.apply {
            when(this){
                0->{stockCount.text="Out of stock"}
                in (1..50)->{stockCount.text="Only $this left " }
                else->{stockCount.text="$this in stock "}
            }
        }
        stockMrpPrice.text="M.R.P: ₹ ${stock.price}"
        val specialPrice = calcPrice(stock.price,stock.discount)
        stockSpecialPrice.text="Special price: ₹ $specialPrice"
        youSaveText.text="you save ₹ ${stock.price-specialPrice} (${stock.discount}%)"



    }

    private fun calcPrice(price:Int,discount:Int):Int{
        return (price - (price * (discount / 100f)).roundToInt())
    }


}