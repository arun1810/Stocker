package com.example.stocker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.stocker.model.StockerDataBase
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.CustomerRepository

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun testuseAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.stocker", appContext.packageName)
    }
    @Test
    fun createDatabase(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //val database  = StockerDataBase(appContext)
        val adminRepository = AdminRepository(appContext)
        val customerRepository = CustomerRepository(appContext,"100")

        customerRepository.placeOrder("qwerty",listOf("001"), listOf(2))

        adminRepository.addStock(Stock(count = 100, stockName = "stock1", stockID = "001", price = 2000, discount = 10))

        print(adminRepository.getAllStock())

        customerRepository.getAllOrderHistory()
    }


}