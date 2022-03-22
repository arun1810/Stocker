package com.example.stocker.repository.baseinterface

import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock

interface BaseCustomerRepository:BaseStockAndOrderHistoryProcessor {

        fun getAllStocks():List<Stock>
        fun placeOrder(
            orderId:String,
            stocks:HashMap<Stock,Int>,
            stockIds:Array<String>,
            stockNames:Array<String>,
            counts:Array<Int>,
            stockPrices:Array<Long>,
            total: Long
        ): Pair<Boolean, OrderHistory>
        fun getAllOrderHistory():List<OrderHistory>


}