package com.example.stocker.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.stocker.model.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stockerhelper.StockTableHelper
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock

class StockerDataBase(
    context: Context,
    private val stockTableHelper:StockTableHelper,
    private val orderHistoryTableHelper: OrderHistoryTableHelper,
    private val customerTableHelper: CustomerTableHelper
    ):SQLiteOpenHelper(context, name,null, version), BaseDataBase {



    private val tag="StockerDB"
    private val adminPassword="pass12345"

    //private var stockTableHeper:StockTableHelper = StockTableHelper()
    //private var orderHistoryTableHelper: OrderHistoryTableHelper = OrderHistoryTableHelper()
    //private var customerTableHelper: CustomerTableHelper = CustomerTableHelper()


    companion object{
        const val name="StockerDB"
        const val version=2
    }

    override fun onCreate(db: SQLiteDatabase?) {

        /*
        create stock,orderHistory and customer table
         */

        if(db!=null){
            stockTableHelper.createTable(db)
            orderHistoryTableHelper.createTable(db)
            customerTableHelper.createTable(db)

            Log.d(tag,"tables are created")
        }




    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${CustomerTableHelper.customerTableName}")
        db?.execSQL("DROP TABLE IF EXISTS ${StockTableHelper.stockTableName}")
        db?.execSQL("DROP TABLE IF EXISTS ${OrderHistoryTableHelper.orderHistoryTableName}")
        onCreate(db)
    }

    override fun addCustomer(customer: Customer): Boolean {
        return customerTableHelper.add(this.writableDatabase,customer)
    }

    override fun deleteCustomer(customers:List<Customer>): Boolean {

        return customerTableHelper.delete(this.writableDatabase,customers)
    }

    override fun updateCustomer(customer: Customer,oldId:String): Boolean {

        return customerTableHelper.update(this.writableDatabase,customer,oldId)
    }

    override fun getAllCustomer(): MutableList<Customer> {

        return customerTableHelper.getAllData(this.readableDatabase)
    }

    override fun validateCustomer(name: String, password: String): Customer? {
       val customer =  customerTableHelper.getCustomer(this.readableDatabase,name)

        return if(customer?.password==password) customer
        else null
    }

    override fun validateAdmin(password: String): Boolean {
        return password==adminPassword
    }

    override fun addStock(stock: Stock): Boolean {
        return stockTableHelper.add(this.writableDatabase,stock)
    }

    override fun deleteStock(stocks:List<Stock>): Boolean {

        return stockTableHelper.delete(this.writableDatabase,stocks)
    }

    override fun updateStock(stock: Stock,oldId:String): Boolean {
        return stockTableHelper.update(this.writableDatabase,stock,oldId)
    }

    override fun updateStocks(stocks: HashMap<Stock,Int>): Boolean {

        return stockTableHelper.updateMultiple(this.writableDatabase,stocks)
    }

    override fun getAllStocks(): MutableList<Stock> {

        return stockTableHelper.getAllData(this.readableDatabase)
    }


    override fun addOrderHistory(orderHistory: OrderHistory):Boolean {
        return orderHistoryTableHelper.add(this.writableDatabase,orderHistory)
    }

    override fun deleteOrderHistory(orders:List<OrderHistory>): Boolean {
        return orderHistoryTableHelper.delete(this.writableDatabase,orders)
    }

    override fun updateOrderHistory(orderHistory: OrderHistory): Boolean {

        return orderHistoryTableHelper.update(this.writableDatabase,orderHistory)
    }

    override fun getAllOrderHistory(): MutableList<OrderHistory> {

        return orderHistoryTableHelper.getAllData(this.readableDatabase)
    }

    override fun getOrderHistoryOfCustomer(customerId: String): MutableList<OrderHistory> {

        return orderHistoryTableHelper.getSpecificData(this.readableDatabase,customerId)
    }
}