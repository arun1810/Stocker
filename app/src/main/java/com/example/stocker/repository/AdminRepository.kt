package com.example.stocker.repository

import android.content.Context
import com.example.stocker.model.BaseDataBase
import com.example.stocker.model.StockerDataBase
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import com.example.stocker.repository.helper.SortUtil
import java.lang.Exception

class AdminRepository(context: Context): BaseAdminRepository, BaseStockAndOrderHistoryProcessor by StockAndOrderHistoryProcessor {

    private val dataBase:BaseDataBase = StockerDataBase(context)


    override fun validateCustomer(name: String, password: String):Customer? {
        return dataBase.validateCustomer(name,password)
    }

    override fun validateAdmin(password: String): Boolean {
        return dataBase.validateAdmin(password)
    }


    override fun createNewCustomer(customer: Customer) {
      dataBase.addCustomer(customer)
    }

    override fun updateCustomer(customer:Customer){
        dataBase.updateCustomer(customer)
    }

    override fun getAllCustomerData(): List<Customer> {
        return dataBase.getAllCustomer()
    }

    override fun getCustomer(customers:MutableList<Customer>,customerId: String): Customer
    {
        return customers.find { customer-> customer.customerId == customerId } ?: throw Exception()
    }

    override fun sortCustomerByName(customers:MutableList<Customer>,order: SortUtil.SortOrder): List<Customer> {
         when(order){
            SortUtil.SortOrder.ASC->{
                 customers.sortBy { customer->customer.name }
            }
            SortUtil.SortOrder.DEC->{
                customers.sortByDescending { customer->customer.name }
            }
        }
        return customers
    }

    override fun filterCustomerByName(customers:MutableList<Customer>,filter: String): List<Customer> {
        return customers.filter { customer -> customer.name.matches(Regex.fromLiteral( "(.*)$filter(.*)" )) }
    }

    override fun getAllOrderHistory():List<OrderHistory> {
        return dataBase.getAllOrderHistory()
    }

    override fun getAllStock(): MutableList<Stock> {
        return dataBase.getAllStocks()
    }

    override fun addStock(stock: Stock): Boolean {
        return dataBase.addStock(stock)
    }

    override fun updateStock(stock:Stock): Boolean {
        return dataBase.updateStock(stock)
    }

    override fun buyStock(stock:Stock): Boolean {
        return dataBase.addStock(stock)
    }

    override fun removeOrderHistory(orders:List<OrderHistory>): Boolean {
        return dataBase.deleteOrderHistory(orders)
    }

    override fun removeCustomer(customers:List<Customer>):Boolean {
        return dataBase.deleteCustomer(customers)
    }

    override fun removeStock(stocks:List<Stock>): Boolean {
        return dataBase.deleteStock(stocks)
    }
}