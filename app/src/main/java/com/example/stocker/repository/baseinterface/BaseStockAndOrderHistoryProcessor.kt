package com.example.stocker.repository.baseinterface

import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.helper.SortUtil

interface BaseStockAndOrderHistoryProcessor {

    fun sortOrderHistoryByTotalPrice(orderHistories:MutableList<OrderHistory>,order: SortUtil.SortOrder):List<OrderHistory>
    fun sortOrderHistoryByDate(orderHistories:MutableList<OrderHistory>,order: SortUtil.SortOrder):List<OrderHistory>
    fun filterOrderHistoryByStockId(orderHistories:MutableList<OrderHistory>,filter:String):List<OrderHistory>


    fun sortStockByPrice(stocks:MutableList<Stock>,order: SortUtil.SortOrder):MutableList<Stock>
    fun sortStockByCount(stocks:MutableList<Stock>,order: SortUtil.SortOrder):MutableList<Stock>
    fun sortStockByName(stocks:MutableList<Stock>,order: SortUtil.SortOrder):MutableList<Stock>
    fun filterStockByName(stocks:MutableList<Stock>,filter:String):MutableList<Stock>

}