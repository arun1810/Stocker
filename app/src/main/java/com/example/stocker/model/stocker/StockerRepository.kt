package com.example.stocker.model.stocker

import com.example.stocker.model.base_interface.BaseRepository
import com.example.stocker.model.stocker.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stocker.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stocker.stockerhelper.StockTableHelper
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock

class StockerRepository(
    private val stockTableHelper:StockTableHelper,
    private val orderHistoryTableHelper: OrderHistoryTableHelper,
    private val customerTableHelper: CustomerTableHelper
    ):BaseRepository {

    private val adminPassword="pass12345"
    //private var stockTableHeper:StockTableHelper = StockTableHelper()
    //private var orderHistoryTableHelper: OrderHistoryTableHelper = OrderHistoryTableHelper()
    //private var customerTableHelper: CustomerTableHelper = CustomerTableHelper()
    companion object{
        const val name="StockerDB"
        const val version=2
    }

    override fun addCustomer(customer: Customer): Boolean {
        return customerTableHelper.add(customer)
    }

    override fun deleteCustomer(customers:List<Customer>): Boolean {

        return customerTableHelper.delete(customers)
    }

    override fun updateCustomer(customer: Customer,oldId:String): Boolean {

        return customerTableHelper.update(customer,oldId)
    }

    override fun getAllCustomer(): MutableList<Customer> {

        return customerTableHelper.getAllData()
    }

    override fun validateCustomer(name: String, password: String): Customer? {
       val customer =  customerTableHelper.getCustomer(name)

        return if(customer?.password==password) customer
        else null
    }

    override fun validateAdmin(password: String): Boolean {
        return password==adminPassword
    }

    override fun addStock(stock: Stock): Boolean {
        return stockTableHelper.add(stock)
    }

    override fun deleteStock(stocks:List<Stock>): Boolean {

        return stockTableHelper.delete(stocks)
    }

    override fun updateStock(stock: Stock,oldId:String): Boolean {
        return stockTableHelper.update(stock,oldId)
    }

    override fun updateStockCount(stocks: HashMap<Stock,Int>): Boolean {

        return stockTableHelper.updateMultiple(stocks)
    }

    override fun getAllStocks(): MutableList<Stock> {

        return stockTableHelper.getAllData()
    }


    override fun addOrderHistory(orderHistory: OrderHistory):Boolean {
        return orderHistoryTableHelper.add(orderHistory)
    }

    override fun deleteOrderHistory(orders:List<OrderHistory>): Boolean {
        return orderHistoryTableHelper.delete(orders)
    }

    override fun updateOrderHistory(orderHistory: OrderHistory): Boolean {

        return orderHistoryTableHelper.update(orderHistory)
    }

    override fun getAllOrderHistory(): MutableList<OrderHistory> {

        return orderHistoryTableHelper.getAllData()
    }

    override fun getOrderHistoryOfCustomer(customerId: String): MutableList<OrderHistory> {

        return orderHistoryTableHelper.getSpecificData(customerId)
    }
}