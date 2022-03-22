package com.example.stocker.di.componants

import com.example.stocker.di.module.CustomerRepoModule
import com.example.stocker.di.scopes.CustomerViewModelScope
import com.example.stocker.repository.baseinterface.BaseCustomerRepository
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.CustomerViewModel
import dagger.Component
import javax.inject.Singleton


@Component(modules = [CustomerRepoModule::class])

@CustomerViewModelScope
interface CustomerRepoComponent {

    fun getCustomerRepository():BaseCustomerRepository

    fun inject(customerViewModel: CustomerViewModel)

}