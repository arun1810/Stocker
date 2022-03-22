package com.example.stocker.di.componants

import android.content.Context
import com.example.stocker.di.module.AppModule
import com.example.stocker.di.module.DatabaseModule
import com.example.stocker.model.BaseDataBase
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.CustomerRepository
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.CustomerViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class,DatabaseModule::class])

@Singleton interface AppComponent {


    fun getContext(): Context

    fun getDataBase():BaseDataBase

    fun injectAdminRepo(adminRepository: AdminRepository)

    fun injectCustomerRepo(customerRepository: CustomerRepository)
}