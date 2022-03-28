package com.example.stocker.model.stocker

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.stocker.model.stocker.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stocker.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stocker.stockerhelper.StockTableHelper
import javax.inject.Inject

class StockerDB(context: Context,application: Application): SQLiteOpenHelper(context, name,null, version) {

    private val tag="StockerDB"

    companion object{
        const val name="StockerDB"
        const val version=2
    }

    @Inject
    lateinit var stockTableHelper:StockTableHelper
    @Inject
    lateinit var orderHistoryTableHelper:OrderHistoryTableHelper
    @Inject
    lateinit var customerTableHelper:CustomerTableHelper

    init {
        //(application as App).getAppComponent().injectStockerDB(this)
    }


    override fun onCreate(db: SQLiteDatabase?) {

        if(db!=null){
            stockTableHelper.createTable()
            orderHistoryTableHelper.createTable()
            customerTableHelper.createTable()

            Log.d(tag,"tables are created")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${CustomerTableHelper.customerTableName}")
        db?.execSQL("DROP TABLE IF EXISTS ${StockTableHelper.stockTableName}")
        db?.execSQL("DROP TABLE IF EXISTS ${OrderHistoryTableHelper.orderHistoryTableName}")
        onCreate(db)
    }
}