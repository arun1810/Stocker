package com.example.stocker.model.room.dao

import androidx.room.*
import com.example.stocker.pojo.OrderHistory

@Dao

abstract class OrderHistoryDao {

    @Query("SELECT * FROM orderhistory")
    abstract fun getAllData(): MutableList<OrderHistory>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun add(orderHistory: OrderHistory)

    @Delete
    abstract fun delete(orderHistory: OrderHistory)

    @Query("SELECT * FROM orderhistory WHERE customerid=:cusId")
    abstract fun getSpecificData(cusId:String):MutableList<OrderHistory>

    @Update(onConflict = OnConflictStrategy.ABORT)
    abstract fun update(orderHistory: OrderHistory)

    @Delete
    abstract fun delete(orders:List<OrderHistory>)
}