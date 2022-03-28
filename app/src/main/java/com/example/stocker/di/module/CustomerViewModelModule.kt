package com.example.stocker.di.module

import android.app.Application
import com.example.stocker.di.scopes.CustomerViewModelScope
import com.example.stocker.repository.CustomerRepository
import com.example.stocker.repository.baseinterface.BaseCustomerRepository
import dagger.Module
import dagger.Provides


@Module
class CustomerViewModelModule(val customerId:String, val application: Application) {

    @Provides
    @CustomerViewModelScope
    fun provideCustomerRepository():BaseCustomerRepository =
        CustomerRepository(customerId = customerId,application)
}