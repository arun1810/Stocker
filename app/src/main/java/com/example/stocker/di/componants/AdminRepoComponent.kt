package com.example.stocker.di.componants

import com.example.stocker.di.module.AdminRepoModule
import com.example.stocker.di.scopes.AdminViewModelScope
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.LoginViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AdminRepoModule::class])
@AdminViewModelScope
interface AdminRepoComponent {

    fun getAdminRepository(): BaseAdminRepository

    fun inject(adminViewModel: AdminViewModel)

    fun injectLoginViewModel(loginViewModel: LoginViewModel)
}