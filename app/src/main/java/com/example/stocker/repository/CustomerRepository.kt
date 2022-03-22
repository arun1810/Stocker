package com.example.stocker.repository

import android.app.Application
import com.example.stocker.App
import com.example.stocker.model.BaseDataBase
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseCustomerRepository
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import java.time.LocalDate
import javax.inject.Inject

class CustomerRepository constructor
    (private val customerId:String,
     application: Application
    ): BaseCustomerRepository, BaseStockAndOrderHistoryProcessor by StockAndOrderHistoryProcessor{


    @Inject
    lateinit var database:BaseDataBase

    //= StockerDataBase(context)

    init {
        (application as App).getAppComponent().injectCustomerRepo(this)
    }

    override fun getAllStocks(): List<Stock> {
        return database.getAllStocks()
    }

    override fun placeOrder(
        orderId:String,
        stocks:HashMap<Stock,Int>,
        stockIds: Array<String>,
        stockNames:Array<String>,
        counts: Array<Int>,
        stockPrices:Array<Long>,
        total: Long
    ):Pair<Boolean,OrderHistory>{
        val orderHistory = OrderHistory(
            orderID = orderId,
            dateOfPurchase = LocalDate.now(),
            customerId = this.customerId,
            stockIds = stockIds,
            stockNames=stockNames,
            counts = counts,
            stockPrices = stockPrices,
            total = total)
        val result = (database.addOrderHistory(orderHistory) && database.updateStocks(stocks))
        return  result to orderHistory
    }

    override fun getAllOrderHistory(): List<OrderHistory> {
        return database.getOrderHistoryOfCustomer(customerId)
    }
}