package com.example.stocker.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.stocker.R
import com.example.stocker.viewmodel.CustomerViewModel


class CustomerActivity : AppCompatActivity() {
    private lateinit var model:CustomerViewModel
    private lateinit var navController:NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        model = ViewModelProvider(this)[CustomerViewModel::class.java]
        navController =  (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController


    }

    override fun onBackPressed() {
          if(navController.currentDestination?.id == R.id.stock_fragment ){
          //  finish()
              super.onBackPressed()
        }
        else{
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        println("customer activity destroyed")
    }



}