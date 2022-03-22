package com.example.stocker.model.stockerhelper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.stocker.pojo.Stock

class StockTableHelper {


    companion object{
        const val stockTableName= "Stock"
        const val id="id"
        const val name="name"
        const val price="price"
        const val discount = "discount"
        const val count="count"

    }

    fun createTable(db:SQLiteDatabase){
        db.execSQL("CREATE TABLE $stockTableName($id TEXT PRIMARY KEY,$name TEXT,$price INTEGER,$discount INTEGER,$count INTEGER)")
    }

    fun getAllData(db:SQLiteDatabase):MutableList<Stock>{
        val stocks:MutableList<Stock> = mutableListOf()

        val cursor = db.rawQuery("SELECT * FROM $stockTableName ",null)

        while(cursor.moveToNext()){
            stocks.add(cursorToStock(cursor))
        }
    return stocks
    }

    fun add(db:SQLiteDatabase,stock:Stock):Boolean{
        return db.insertOrThrow(stockTableName,"null",stockToContentValues(stock)) >0
    }

    fun delete(db:SQLiteDatabase,stocks:List<Stock>):Boolean{

        db.beginTransaction()
        var result=false

        for(stock in stocks){
            if(db.delete(stockTableName,"id=?", arrayOf(stock.stockID)) >0){
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

    fun update(db:SQLiteDatabase,stock:Stock,oldId:String):Boolean{
        return db.updateWithOnConflict(stockTableName,stockToContentValues(stock),"$id=?",
            arrayOf(oldId),SQLiteDatabase.CONFLICT_FAIL) >0

        //return db.replaceOrThrow(stockTableName,null,stockToContentValues(stock)) >0
    }

    fun updateMultiple(db:SQLiteDatabase,stocks:HashMap<Stock,Int>):Boolean{
        var result=false
        db.beginTransaction()
        for((stock,count)in stocks ){
            stock.count-=count
           if(update(db,stock,stock.stockID)){
              result=true
           }
            else{
                result=false
                break
            }
        }
        if(result){
            db.setTransactionSuccessful()
        }
        db.endTransaction()
        return result
    }

    private fun cursorToStock(cursor: Cursor) = Stock(
        stockID = cursor.getString(0),
        stockName =cursor.getString(1),
        price =cursor.getInt(2),
        discount = cursor.getInt(3),
        count =cursor.getInt(4)

    )

    private fun stockToContentValues(stock:Stock)=  ContentValues().apply {
            put(id,stock.stockID)
            put(name, stock.stockName)
            put(price,stock.price)
            put(discount,stock.discount)
            put(count,stock.count)
    }
}