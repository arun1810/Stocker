package com.example.stocker.repository.baseinterface

import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock

interface BaseCustomerRepository {

        fun getAllStocks():List<Stock>
        fun placeOrder(orderId:String,stocks:HashMap<Stock,Int>,stockIds:Array<String>,counts:Array<Int>,total:Long): Pair<Boolean, OrderHistory>
        fun getAllOrderHistory():List<OrderHistory>


}