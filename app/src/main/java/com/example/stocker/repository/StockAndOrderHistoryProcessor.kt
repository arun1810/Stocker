package com.example.stocker.repository

import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.baseinterface.BaseStockAndOrderHistoryProcessor
import com.example.stocker.repository.helper.SortUtil
import java.util.regex.Pattern

object StockAndOrderHistoryProcessor : BaseStockAndOrderHistoryProcessor {

    init {
        println("viewer created")
    }

    override fun sortOrderHistoryByTotalPrice(
        orderHistories: MutableList<OrderHistory>,
        order: SortUtil.SortOrder
    ): List<OrderHistory> {

        val temp = mutableListOf<OrderHistory>().apply {
            addAll(orderHistories)
        }

        when (order) {

            SortUtil.SortOrder.ASC -> {
                temp.sortBy { orderHistory -> orderHistory.total }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { orderHistory -> orderHistory.total }
            }
        }

        return temp

    }

    override fun sortOrderHistoryByDate(
        orderHistories: MutableList<OrderHistory>,
        order: SortUtil.SortOrder
    ): List<OrderHistory> {

        val temp = mutableListOf<OrderHistory>().apply {
            addAll(orderHistories)
        }

        when (order) {

            SortUtil.SortOrder.ASC -> {
                temp.sortBy { orderHistory -> orderHistory.dateOfPurchase }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { orderHistory -> orderHistory.dateOfPurchase }
            }
        }

        return temp

    }

    override fun filterOrderHistoryByStockId(
        orderHistories: MutableList<OrderHistory>,
        filter: String
    ): List<OrderHistory> {

        return orderHistories.filter { orderHistory -> orderHistory.stockIds.contains(filter) }
    }

    override fun sortStockByPrice(
        stocks: MutableList<Stock>,
        order: SortUtil.SortOrder
    ): MutableList<Stock> {
        val temp = mutableListOf<Stock>().apply {
            addAll(stocks)
        }

        when (order) {
            SortUtil.SortOrder.ASC -> {
                temp.sortBy { stock -> stock.price }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { stock -> stock.price }
            }
        }

        return temp
    }

    override fun sortStockByCount(
        stocks: MutableList<Stock>,
        order: SortUtil.SortOrder
    ): MutableList<Stock> {

        val temp = mutableListOf<Stock>().apply {
            addAll(stocks)
        }

        when (order) {

            SortUtil.SortOrder.ASC -> {
                temp.sortBy { stock -> stock.count }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { stock -> stock.count }
            }
        }

        return temp
    }

    override fun sortStockByName(
        stocks: MutableList<Stock>,
        order: SortUtil.SortOrder
    ): MutableList<Stock> {
        val temp = mutableListOf<Stock>().apply {
            addAll(stocks)
        }

        when (order) {

            SortUtil.SortOrder.ASC -> {
                temp.sortBy { stock -> stock.stockName }
            }
            SortUtil.SortOrder.DEC -> {
                temp.sortByDescending { stock -> stock.stockName }
            }
        }
        return temp
    }

    override fun filterStockByName(stocks: MutableList<Stock>, filter: String): MutableList<Stock> {
        val pattern = Pattern.compile("(.*?)${filter.lowercase()}(.*?)")
        return stocks.filter { stock -> pattern.matcher(stock.stockName.lowercase()).matches() }
            .toMutableList()
    }

}