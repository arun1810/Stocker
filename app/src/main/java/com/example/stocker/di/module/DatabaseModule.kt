package com.example.stocker.di.module

import android.content.Context
import com.example.stocker.di.componants.AppComponent
import com.example.stocker.model.BaseDataBase
import com.example.stocker.model.StockerDataBase
import com.example.stocker.model.stockerhelper.CustomerTableHelper
import com.example.stocker.model.stockerhelper.OrderHistoryTableHelper
import com.example.stocker.model.stockerhelper.StockTableHelper
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module(includes = [DatabaseHelperModule::class])
class DatabaseModule(val context: Context){

        @Singleton
        @Provides
        fun provideDataBase(customerTableHelper: CustomerTableHelper, orderHistoryTableHelper: OrderHistoryTableHelper, stockTableHelper: StockTableHelper):BaseDataBase{
            return StockerDataBase(context,stockTableHelper, orderHistoryTableHelper, customerTableHelper)
        }


}