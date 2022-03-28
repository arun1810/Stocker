package com.example.stocker

import android.app.Application
import com.example.stocker.di.componants.AppComponent
import com.example.stocker.di.componants.DaggerAppComponent
import com.example.stocker.di.module.AppModule

class App:Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

       appComponent =  DaggerAppComponent.builder()
           .appModule(AppModule(baseContext,this))
           //.stockerDatabaseModule(StockerDatabaseModule(baseContext))
           //.roomDataBaseModule(RoomDataBaseModule())
           .build()
    }

    fun getAppComponent() = appComponent
}