package com.example.stocker.di.module

import android.app.Application
import com.example.stocker.di.scopes.AdminViewModelScope
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AdminRepoModule(val application: Application) {

    @Provides
    @AdminViewModelScope
    fun provideAdminRepository():BaseAdminRepository = AdminRepository(application)
}