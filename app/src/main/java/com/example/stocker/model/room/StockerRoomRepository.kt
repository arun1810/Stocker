package com.example.stocker.model.room

import com.example.stocker.model.base_interface.BaseRepository
import com.example.stocker.model.room.dao.CustomerDao
import com.example.stocker.model.room.dao.OrderHistoryDao
import com.example.stocker.model.room.dao.StockDao
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock


class StockerRoomRepository(private val stockDao: StockDao, private val customerDao: CustomerDao, private val orderHistoryDao: OrderHistoryDao): BaseRepository {
    private val adminPassword="pass12345"


    init {
        print("Stocker Room Repo Initialized")
    }


    override fun addCustomer(customer: Customer): Boolean {
       customerDao.add(customer)
        return true
    }

    override fun deleteCustomer(customers: List<Customer>): Boolean {
        customerDao.deleteListOfCustomer(customers)
        return true
    }

    override fun updateCustomer(customer: Customer, oldId: String): Boolean {
        customerDao.update(customerId = customer.customerId, name = customer.name, password = customer.password,gender= customer.gender,dob=customer.dob.toString(), mobileNumber = customer.mobile_number, oldId = oldId)
        return true
    }

    override fun getAllCustomer(): MutableList<Customer> {
        return customerDao.getAllData()
    }

    override fun validateCustomer(name: String, password: String): Customer? {
        val customer =  customerDao.getCustomer(name)

        return if(customer?.password ==password) customer else null
    }

    override fun validateAdmin(password: String): Boolean {
        return password==adminPassword
    }

    override fun addStock(stock: Stock): Boolean {
        stockDao.add(stock)
        return true
    }

    override fun deleteStock(stocks: List<Stock>): Boolean {
        stockDao.deleteListOfStocks(stocks)
        return true
    }

    override fun updateStock(stock: Stock, oldId: String): Boolean {
        stockDao.update(stockId = stock.stockID, name = stock.stockName, price = stock.price, discount = stock.discount, count = stock.count, oldId = oldId)
        return true
    }

    override fun updateStockCount(stocks: HashMap<Stock, Int>): Boolean {
        stockDao.updateStocksCount(stocks)
        return true
    }

    override fun getAllStocks(): MutableList<Stock> {
        return stockDao.getAllData()
    }

    override fun addOrderHistory(orderHistory: OrderHistory): Boolean {
        orderHistoryDao.add(orderHistory)
        return true
    }

    override fun deleteOrderHistory(orders: List<OrderHistory>): Boolean {
        orderHistoryDao.delete(orders)
        return true
    }

    override fun updateOrderHistory(orderHistory: OrderHistory): Boolean {
        orderHistoryDao.update(orderHistory)
        return true
    }

    override fun getAllOrderHistory(): MutableList<OrderHistory> {
        return orderHistoryDao.getAllData()
    }

    override fun getOrderHistoryOfCustomer(customerId: String): MutableList<OrderHistory> {
        return orderHistoryDao.getSpecificData(customerId)
    }
}