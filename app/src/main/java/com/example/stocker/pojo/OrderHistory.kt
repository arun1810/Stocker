package com.example.stocker.pojo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stocker.model.room.type_converters.DateTypeConverter
import com.example.stocker.model.room.type_converters.OrderHistoryTypeConverter
import java.io.Serializable
import java.time.LocalDate

@Entity
@TypeConverters(OrderHistoryTypeConverter::class,DateTypeConverter::class)
data class OrderHistory(
    @PrimaryKey @ColumnInfo(name = "id") @NonNull val orderID:String,
    @ColumnInfo(name = "dataOfPurchase") @NonNull val dateOfPurchase:LocalDate,
    @ColumnInfo(name = "customerid") @NonNull val customerId:String,
    @ColumnInfo(name = "stockids") @NonNull val stockIds:Array<String>,
    @ColumnInfo(name = "stocknames") @NonNull val stockNames:Array<String>,
    @ColumnInfo(name= "counts") @NonNull val counts:Array<Int>,
    @ColumnInfo(name = "stockprices") @NonNull val stockPrices:Array<Long>,
    @ColumnInfo(name = "total") @NonNull val total: Long
) :Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderHistory

        if (orderID != other.orderID) return false
        if (dateOfPurchase != other.dateOfPurchase) return false
        if (customerId != other.customerId) return false
        if (!stockIds.contentEquals(other.stockIds)) return false
        if (!stockNames.contentEquals(other.stockNames)) return false
        if (!counts.contentEquals(other.counts)) return false
        if (!stockPrices.contentEquals(other.stockPrices)) return false
        if (total != other.total) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderID.hashCode()
        result = 31 * result + dateOfPurchase.hashCode()
        result = 31 * result + customerId.hashCode()
        result = 31 * result + stockIds.contentHashCode()
        result = 31 * result + stockNames.contentHashCode()
        result = 31 * result + counts.contentHashCode()
        result = 31 * result + stockPrices.contentHashCode()
        result = 31 * result + total.hashCode()
        return result
    }


}
