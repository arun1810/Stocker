package com.example.stocker.repository.baseinterface

import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.helper.SortUtil

interface BaseAdminRepository :BaseStockAndOrderHistoryProcessor{

    fun validateCustomer(name:String,password:String): Customer?
    fun validateAdmin(password:String):Boolean
    fun createNewCustomer(customer: Customer)
    fun getAllCustomerData():List<Customer>
    fun getCustomer(customers:MutableList<Customer>,customerId:String):Customer
    fun sortCustomerByName(customers:MutableList<Customer>,order: SortUtil.SortOrder):List<Customer>
    fun sortCustomerByDOB(customers:MutableList<Customer>,order: SortUtil.SortOrder):List<Customer>
    fun filterCustomerByName(customers:MutableList<Customer>,filter:String):List<Customer>

    fun getAllOrderHistory(): List<OrderHistory>

    fun getAllStock():List<Stock>
    fun addStock(stock: Stock): Boolean
    fun updateStock(stock:Stock,oldId:String): Boolean
    fun buyStock(stock:Stock): Boolean

    fun removeOrderHistory(orders:List<OrderHistory>): Boolean
    fun removeCustomer(customers: List<Customer>): Boolean
    fun removeStock(stocks:List<Stock>): Boolean

    fun updateCustomer(customer: Customer,oldId: String)
}