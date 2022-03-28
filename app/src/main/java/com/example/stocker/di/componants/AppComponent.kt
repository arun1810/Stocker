package com.example.stocker.di.componants

import android.app.Application
import android.content.Context
import com.example.stocker.di.module.AppModule
import com.example.stocker.di.module.RoomDataBaseModule
import com.example.stocker.model.base_interface.BaseRepository
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.CustomerRepository
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class,RoomDataBaseModule::class])

@Singleton interface AppComponent {


    fun getContext(): Context

    fun getApplication():Application

    fun getBaseRepository(): BaseRepository

    fun injectAdminRepo(adminRepository: AdminRepository)

    fun injectCustomerRepo(customerRepository: CustomerRepository)

    fun injectRoomRepo(roomRepository: BaseRepository)

    //fun injectStockerDB(stockerDB: StockerDB)
}