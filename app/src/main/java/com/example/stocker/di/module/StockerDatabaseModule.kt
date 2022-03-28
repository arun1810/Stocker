package com.example.stocker.di.module

import android.content.Context
import com.example.stocker.model.base_interface.BaseRepository
import com.example.stocker.model.stocker.StockerRepository
import com.example.stocker.model.stocker.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stocker.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stocker.stockerhelper.StockTableHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseHelperModule::class])
class StockerDatabaseModule{

    companion object {
        @Singleton
        @Provides
        fun provideDataBase(
            customerTableHelper: CustomerTableHelper,
            orderHistoryTableHelper: OrderHistoryTableHelper,
            stockTableHelper: StockTableHelper
        ): BaseRepository {
            return StockerRepository(
                stockTableHelper,
                orderHistoryTableHelper,
                customerTableHelper
            )
        }
    }

}