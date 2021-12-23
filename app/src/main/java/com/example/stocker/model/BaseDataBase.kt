package com.example.stocker.model

import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock

interface BaseDataBase {

    fun addCustomer(customer: Customer): Boolean
    fun deleteCustomer(customers:List<Customer>): Boolean
    fun updateCustomer(customer:Customer): Boolean
    fun getAllCustomer():MutableList<Customer>
    fun validateCustomer(name:String,password:String): Customer?
    fun validateAdmin(password:String):Boolean

    fun addStock(stock: Stock): Boolean
    fun deleteStock(stocks:List<Stock>): Boolean
    fun updateStock(stock:Stock): Boolean
    fun updateStocks(stocks:HashMap<Stock,Int>):Boolean
    fun getAllStocks():MutableList<Stock>
    fun placeOrder(orderHistory: OrderHistory):Boolean

    fun addOrderHistory(orderHistory:OrderHistory): Boolean
    fun deleteOrderHistory(orders:List<OrderHistory>): Boolean
    fun updateOrderHistory(orderHistory: OrderHistory): Boolean
    fun getAllOrderHistory():MutableList<OrderHistory>
    fun getOrderHistoryOfCustomer(customerId:String):MutableList<OrderHistory>

}