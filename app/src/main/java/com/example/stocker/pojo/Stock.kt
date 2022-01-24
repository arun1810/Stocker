package com.example.stocker.pojo

import java.io.Serializable

data class Stock(val stockID:String, val stockName:String, val price: Int, var count: Int, val discount:Int):Serializable