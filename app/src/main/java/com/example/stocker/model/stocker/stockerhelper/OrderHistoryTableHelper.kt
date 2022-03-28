package com.example.stocker.model.stocker.stockerhelper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.stocker.model.stocker.StockerDB
import com.example.stocker.pojo.OrderHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class OrderHistoryTableHelper(private val db: StockerDB){
    companion object{
        const val id="id"
        const val orderHistoryTableName= "OrderHistory"
        const val dateOfPurchase = "dataOfPurchase"
        const val customerId="customerid"
        const val stockIds="stockids"
        const val stockPrices="stockprices"
        const val stockNames="stocknames"
        const val counts="counts"
        const val total="total"
    }

    fun createTable(){
        db.writableDatabase.execSQL("CREATE TABLE $orderHistoryTableName(" +
                "$id TEXT PRIMARY KEY," +
                "$dateOfPurchase TEXT," +
                "$customerId TEXT," +
                "$stockIds TEXT," +
                "$stockNames TEXT," +
                "$counts TEXT," +
                "$stockPrices TEXT," +
                "$total INTEGER)"
        )
    }

    fun getAllData():MutableList<OrderHistory>{
        val orderHistories = mutableListOf<OrderHistory>()
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM $orderHistoryTableName",null)

        while(cursor.moveToNext()){
            orderHistories.add(cursorToOrderHistory(cursor))
        }
      return orderHistories
    }

    fun getSpecificData(cusId:String):MutableList<OrderHistory>{
        val orderHistories = mutableListOf<OrderHistory>()
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM $orderHistoryTableName WHERE ${customerId}=?",
            arrayOf(cusId)
        )
        while(cursor.moveToNext()){
            orderHistories.add(cursorToOrderHistory(cursor))

        }

        return orderHistories
    }

    fun add(orderHistory:OrderHistory):Boolean{
        return db.writableDatabase.insertWithOnConflict(orderHistoryTableName,"null",orderHistoryToContentValues(orderHistory),SQLiteDatabase.CONFLICT_IGNORE)!=-1L
    }

    fun delete(orders:List<OrderHistory>):Boolean{

        val db = db.writableDatabase
        db.beginTransaction()
        var result=false

        for(orderHistory in orders){
            if(db.delete(orderHistoryTableName,"id=?", arrayOf(orderHistory.orderID)) >0){
                result=true
            }
            else{
                result=false
                break
            }
        }
        if(result){db.setTransactionSuccessful()}
        db.endTransaction()
        return result
    }

    fun update(orderHistory:OrderHistory):Boolean{
        return db.writableDatabase.update(orderHistoryTableName,orderHistoryToContentValues(orderHistory),null,null) >0
    }



    private fun cursorToOrderHistory(cursor:Cursor)= OrderHistory(
        orderID = cursor.getString(0),
        dateOfPurchase = LocalDate.parse(cursor.getString(1), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        customerId =cursor.getString(2),
        stockIds = stringToStringArray(cursor.getString(3)).toTypedArray(),
        stockNames = stringToStringArray(cursor.getString(4)).toTypedArray(),
        counts =stringToIntArray(cursor.getString(5)).toTypedArray(),
        stockPrices = stringToLongArray(cursor.getString(6)).toTypedArray(),
        total = cursor.getLong(7)
    )

    private fun stringToStringArray(str:String):List<String>{
        return str.split("-")
    }

    private fun arrayToString(strs:Array<*>):String{
        return StringBuilder().apply {
            for(i in strs.indices){
                append(strs[i])
                if(i<strs.size-1) append("-")
            }
        }.toString()
    }

    private fun stringToIntArray(str:String):List<Int>{
       return stringToStringArray(str).stream().map { data->data.toInt() }.collect(Collectors.toList())
    }
    private fun stringToLongArray(str:String):List<Long>{
        return stringToStringArray(str).stream().map { data->data.toLong() }.collect(Collectors.toList())
    }

    private fun orderHistoryToContentValues(orderHistory:OrderHistory) = ContentValues().apply {
        put(id,orderHistory.orderID)
        put(dateOfPurchase,orderHistory.dateOfPurchase.toString())
        put(customerId,orderHistory.customerId)
        put(stockIds,arrayToString(orderHistory.stockIds))
        put(stockNames,arrayToString(orderHistory.stockNames))
        put(counts,arrayToString(orderHistory.counts))
        put(stockPrices,arrayToString(orderHistory.stockPrices))
        put(total,orderHistory.total)
    }



}