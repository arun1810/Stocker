package com.example.stocker.repository

import android.content.Context
import com.example.stocker.model.BaseDataBase
import com.example.stocker.model.StockerDataBase
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseCustomerRepository
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomerRepository(context: Context, private val customerId:String): BaseCustomerRepository, BaseStockAndOrderHistoryProcessor by StockAndOrderHistoryProcessor{
    private val database:BaseDataBase = StockerDataBase(context)

    override fun getAllStocks(): List<Stock> {
        return database.getAllStocks()
    }

    override fun placeOrder(orderId:String,stocks:HashMap<Stock,Int>,stockIds: Array<String>, counts: Array<Int>,total:Long):Pair<Boolean,OrderHistory>{
        val orderHistory = OrderHistory(orderID = orderId, dateOfPurchase = LocalDate.now(), customerId = this.customerId, stockIds = stockIds, counts = counts, total = total)
        val result = (database.addOrderHistory(orderHistory) && database.updateStocks(stocks))
        return  result to orderHistory
    }

    override fun getAllOrderHistory(): List<OrderHistory> {
        return database.getOrderHistoryOfCustomer(customerId)
    }
}