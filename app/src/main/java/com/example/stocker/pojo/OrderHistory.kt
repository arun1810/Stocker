package com.example.stocker.pojo

import java.time.LocalDate

data class OrderHistory(
    val orderID:String,
    val dateOfPurchase:LocalDate,
    val customerId:String,
    val stockIds:Array<String>,
    val counts:Array<Int>,
    val total: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderHistory

        if (orderID != other.orderID) return false
        if (dateOfPurchase != other.dateOfPurchase) return false
        if (customerId != other.customerId) return false
        if (!stockIds.contentEquals(other.stockIds)) return false
        if (!counts.contentEquals(other.counts)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderID.hashCode()
        result = 31 * result + dateOfPurchase.hashCode()
        result = 31 * result + customerId.hashCode()
        result = 31 * result + stockIds.contentHashCode()
        result = 31 * result + counts.contentHashCode()
        result = 31 * result + total.hashCode()
        return result
    }
}
