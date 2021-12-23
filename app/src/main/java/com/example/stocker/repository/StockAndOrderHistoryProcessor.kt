package com.example.stocker.repository

import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import com.example.stocker.repository.helper.SortUtil
import java.util.regex.Pattern

object StockAndOrderHistoryProcessor: BaseStockAndOrderHistoryProcessor {

    init {
        println("viewer created")
    }

    override fun sortOrderHistoryByTotalPrice(
        orderHistories: MutableList<OrderHistory>,
        order: SortUtil.SortOrder
    ): List<OrderHistory> {

         when(order){

            SortUtil.SortOrder.ASC->{
                orderHistories.sortBy { orderHistory -> orderHistory.total }
            }
            SortUtil.SortOrder.DEC->{
                orderHistories.sortByDescending { orderHistory -> orderHistory.total }
            }
        }

        return orderHistories

    }

    override fun sortOrderHistoryByDate(
        orderHistories: MutableList<OrderHistory>,
        order: SortUtil.SortOrder
    ): List<OrderHistory> {

        when(order){

            SortUtil.SortOrder.ASC->{
                orderHistories.sortBy { orderHistory -> orderHistory.dateOfPurchase }
            }
            SortUtil.SortOrder.DEC->{
                orderHistories.sortByDescending { orderHistory -> orderHistory.dateOfPurchase }
            }
        }

        return orderHistories

    }

    override fun filterOrderHistoryByStockId(
        orderHistories: MutableList<OrderHistory>,
        filter: String
    ): List<OrderHistory> {

       return orderHistories.filter { orderHistory->orderHistory.stockIds.contains(filter) }
    }

    override fun sortStockByPrice(stocks: MutableList<Stock>, order: SortUtil.SortOrder): MutableList<Stock> {
         when(order){

            SortUtil.SortOrder.ASC->{
                stocks.sortBy { stock->stock.price }
            }
            SortUtil.SortOrder.DEC->{
                stocks.sortByDescending { stock->stock.price }
            }
        }

        return stocks
    }

    override fun sortStockByCount(stocks: MutableList<Stock>, order: SortUtil.SortOrder): MutableList<Stock> {
         when(order){

            SortUtil.SortOrder.ASC->{
                stocks.sortBy { stock->stock.count }
            }
            SortUtil.SortOrder.DEC->{
                stocks.sortByDescending { stock->stock.count }
            }
        }

        return stocks
    }

    override fun sortStockByName(
        stocks: MutableList<Stock>,
        order: SortUtil.SortOrder
    ): MutableList<Stock> {
       when(order){

            SortUtil.SortOrder.ASC->{
                stocks.sortBy { stock->stock.stockName }
            }
            SortUtil.SortOrder.DEC->{
                stocks.sortByDescending { stock->stock.stockName }
            }
        }
        return stocks
    }

    override fun filterStockByName(stocks: MutableList<Stock>, filter: String): MutableList<Stock> {
        val pattern = Pattern.compile("(.*?)$filter(.*?)")
        return stocks.filter { stock->pattern.matcher(stock.stockName).matches() }.toMutableList()
    }

}