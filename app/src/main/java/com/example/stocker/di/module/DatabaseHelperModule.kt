package com.example.stocker.di.module

import android.app.Application
import android.content.Context
import com.example.stocker.model.stocker.StockerDB
import com.example.stocker.model.stocker.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stocker.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stocker.stockerhelper.StockTableHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseHelperModule {

    companion object {

        @Singleton
        @Provides

        fun provideStockerDatabase(context: Context,application: Application):StockerDB{
            return StockerDB(context,application)
        }


        @Singleton
        @Provides
        fun provideCustomerTableHelper(db:StockerDB): CustomerTableHelper {
            return CustomerTableHelper(db)
        }

        @Singleton
        @Provides
        fun provideOrderHistoryTableHelper(db:StockerDB): OrderHistoryTableHelper {
            return OrderHistoryTableHelper(db)
        }

        @Singleton
        @Provides
        fun provideStockTableHelper(db:StockerDB): StockTableHelper {
            return StockTableHelper(db)
        }
    }
}