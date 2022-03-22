package com.example.stocker.di.module

import com.example.stocker.model.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stockerhelper.StockTableHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseHelperModule {

    companion object {

        @Singleton
        @Provides
        fun provideCustomerTableHelper(): CustomerTableHelper {
            return CustomerTableHelper()
        }

        @Singleton
        @Provides
        fun provideOrderHistoryTableHelper(): OrderHistoryTableHelper {
            return OrderHistoryTableHelper()
        }

        @Singleton
        @Provides
        fun provideStockTableHelper(): StockTableHelper {
            return StockTableHelper()
        }
    }
}