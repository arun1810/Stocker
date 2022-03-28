package com.example.stocker.model.room.dao

import androidx.room.*
import com.example.stocker.pojo.Stock

@Dao
abstract class StockDao {

    @Query("SELECT * FROM Stock")
    abstract fun getAllData(): MutableList<Stock>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(stock:Stock)

    @Delete
    abstract fun delete(stock: Stock)


    @Transaction
    open fun deleteListOfStocks(stocks:List<Stock>){
        for(stock in stocks){
            delete(stock)
        }
    }

    @Query("UPDATE stock set count=:newCount WHERE id =:stockId")
    abstract fun updateStockCount(stockId:String,newCount:Int)


    @Transaction
    open fun updateStocksCount(stocksAndCounts:HashMap<Stock,Int>){
        for((stock,count) in stocksAndCounts){
            updateStockCount(stockId =stock.stockID, newCount = count )
        }
    }

    @Query("UPDATE stock SET id =:stockId,name=:name,price=:price,count=:count,discount=:discount WHERE id =:oldId")
    abstract fun update(stockId: String, name: String?, price: Int?, count: Int?, discount: Int?, oldId:String)


}