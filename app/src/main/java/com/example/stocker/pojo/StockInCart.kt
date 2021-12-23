package com.example.stocker.pojo

data class StockInCart(val stocksNames:Array<String>,val stockIds:Array<String>,val counts:Array<Int>,val price:Array<String>,val total:Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StockInCart

        if (!stocksNames.contentEquals(other.stocksNames)) return false
        if (!stockIds.contentEquals(other.stockIds)) return false
        if (!counts.contentEquals(other.counts)) return false
        if (!price.contentEquals(other.price)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stocksNames.contentHashCode()
        result = 31 * result + stockIds.contentHashCode()
        result = 31 * result + counts.contentHashCode()
        result = 31 * result + price.contentHashCode()
        return result
    }
}
