package com.example.stocker.di.componants

import com.example.stocker.di.module.CustomerViewModelModule
import com.example.stocker.di.scopes.CustomerViewModelScope
import com.example.stocker.repository.baseinterface.BaseCustomerRepository
import com.example.stocker.viewmodel.CustomerViewModel
import dagger.Component


@Component(modules = [CustomerViewModelModule::class])

@CustomerViewModelScope
interface CustomerViewModelComponent {

    fun getCustomerRepository():BaseCustomerRepository

    fun inject(customerViewModel: CustomerViewModel)

}