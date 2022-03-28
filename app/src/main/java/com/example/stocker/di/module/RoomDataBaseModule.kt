package com.example.stocker.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.stocker.model.base_interface.BaseRepository
import com.example.stocker.model.room.RoomDB
import com.example.stocker.model.room.StockerRoomRepository
import com.example.stocker.model.room.dao.CustomerDao
import com.example.stocker.model.room.dao.OrderHistoryDao
import com.example.stocker.model.room.dao.StockDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomDataBaseModule() {

    companion object {

        @Provides
        @Singleton
        fun provideRoomRepo(roomDB: RoomDB): BaseRepository {
            return StockerRoomRepository(
                roomDB.stockDao(),
                roomDB.customerDao(),
                roomDB.orderHistoryDao()
            )
        }

        @Provides
        @Singleton
        fun provideRoomDB(context: Context): RoomDB {
            return Room.databaseBuilder(context, RoomDB::class.java, "StockerDB2.db")
                //.addMigrations(MIGRATION_1_2)
                .addCallback(RoomDB.RoomDBCallback())
                .build()
        }

        @Provides
        @Singleton
        fun provideStockDao(roomDB: RoomDB): StockDao {
            return roomDB.stockDao()
        }

        @Provides
        @Singleton
        fun provideCustomerDao(roomDB: RoomDB): CustomerDao {
            return roomDB.customerDao()
        }

        @Provides
        @Singleton
        fun provideOrderHistoryDao(roomDB: RoomDB): OrderHistoryDao {
            return roomDB.orderHistoryDao()
        }

    }

}