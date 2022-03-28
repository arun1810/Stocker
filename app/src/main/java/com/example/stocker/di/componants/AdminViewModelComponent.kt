package com.example.stocker.di.componants

import com.example.stocker.di.module.AdminViewModelModule
import com.example.stocker.di.scopes.AdminViewModelScope
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.LoginViewModel
import dagger.Component

@Component(modules = [AdminViewModelModule::class])
@AdminViewModelScope
interface AdminViewModelComponent {

    fun getAdminRepository(): BaseAdminRepository

    fun inject(adminViewModel: AdminViewModel)

    fun injectLoginViewModel(loginViewModel: LoginViewModel)
}