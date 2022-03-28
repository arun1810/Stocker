package com.example.stocker.pojo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Stock",
    //indices = [Index(value = ["id","name","price","discount","count"])]
)

data class Stock(
    @PrimaryKey @ColumnInfo(name="id") @NonNull val stockID:String,
    @ColumnInfo(name = "name",) @NonNull val stockName:String,
    @ColumnInfo(name = "price") @NonNull val price: Int,
    @ColumnInfo(name = "discount") @NonNull val discount:Int,
    @ColumnInfo(name = "count") @NonNull var count: Int

    ):Serializable