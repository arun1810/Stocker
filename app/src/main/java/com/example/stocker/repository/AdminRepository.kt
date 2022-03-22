package com.example.stocker.repository

import android.app.Application
import com.example.stocker.App
import com.example.stocker.model.BaseDataBase
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import com.example.stocker.repository.helper.SortUtil
import java.lang.Exception
import java.util.regex.Pattern
import javax.inject.Inject

class AdminRepository constructor(
    application: Application
) : BaseAdminRepository,
    BaseStockAndOrderHistoryProcessor by StockAndOrderHistoryProcessor {


    @Inject
    lateinit var dataBase: BaseDataBase
    //= StockerDataBase(context, stockTableHelper = stockTableHelper, orderHistoryTableHelper = orderHistoryTableHelper, customerTableHelper = customerTableHelper)

    init {

       (application as App).getAppComponent().injectAdminRepo(this)
    }
    override fun validateCustomer(name: String, password: String): Customer? {
        return dataBase.validateCustomer(name, password)
    }

    override fun validateAdmin(password: String): Boolean {
        return dataBase.validateAdmin(password)
    }


    override fun createNewCustomer(customer: Customer) {
        dataBase.addCustomer(customer)
    }

    override fun updateCustomer(customer: Customer, oldId: String) {
        dataBase.updateCustomer(customer, oldId)
    }

    override fun getAllCustomerData(): List<Customer> {
        return dataBase.getAllCustomer()
    }

    override fun getCustomer(customers: MutableList<Customer>, customerId: String): Customer {
        return customers.find { customer -> customer.customerId == customerId } ?: throw Exception()
    }

    override fun sortCustomerByName(
        customers: MutableList<Customer>,
        order: SortUtil.SortOrder
    ): List<Customer> {
        val temp = mutableListOf<Customer>().apply { addAll(customers) }
        when (order) {
            SortUtil.SortOrder.ASC -> {
                temp.sortBy { customer -> customer.name }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { customer -> customer.name }
            }
        }
        return temp
    }

    override fun sortCustomerByDOB(
        customers: MutableList<Customer>,
        order: SortUtil.SortOrder
    ): List<Customer> {

        val temp = mutableListOf<Customer>().apply { addAll(customers) }
        when (order) {
            SortUtil.SortOrder.ASC -> {
                temp.sortBy { customer -> customer.dob }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { customer -> customer.dob }
            }
        }
        return temp
    }

    override fun filterCustomerByName(
        customers: MutableList<Customer>,
        filter: String
    ): List<Customer> {
        val pattern = Pattern.compile("(.*?)${filter.lowercase()}(.*?)")
        return customers.filter { customer -> pattern.matcher(customer.name.lowercase()).matches() }
    }

    override fun getAllOrderHistory(): List<OrderHistory> {
        return dataBase.getAllOrderHistory()
    }

    override fun getAllStock(): MutableList<Stock> {
        return dataBase.getAllStocks()
    }

    override fun addStock(stock: Stock): Boolean {
        return dataBase.addStock(stock)
    }

    override fun updateStock(stock: Stock, oldId: String): Boolean {
        return dataBase.updateStock(stock, oldId)
    }

    override fun buyStock(stock: Stock): Boolean {
        return dataBase.addStock(stock)
    }

    override fun removeOrderHistory(orders: List<OrderHistory>): Boolean {
        return dataBase.deleteOrderHistory(orders)
    }

    override fun removeCustomer(customers: List<Customer>): Boolean {
        return dataBase.deleteCustomer(customers)
    }

    override fun removeStock(stocks: List<Stock>): Boolean {
        return dataBase.deleteStock(stocks)
    }
}