package com.example.stocker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner





@RunWith(MockitoJUnitRunner::class)
class MockTest {


    @Test
    fun testAdminViewModel(){
        //val mockApplication = Mockito.mock(AdminActivity::class.java)
        val context = ApplicationProvider.getApplicationContext<Context>()
        //val model = AdminViewModel(context.applicationContext)
        //model.getAllOrderHistory()
    }
}