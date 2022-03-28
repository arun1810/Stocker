package com.example.stocker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.stocker.model.stocker.StockerRepository
import org.junit.Test

import org.junit.Assert.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun createDatabase(){
        //val database  = StockerRepository(context)



    }
}