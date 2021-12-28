package com.example.stocker.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.Stock
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.fragments.util.GsonHelper
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val str = SharedPreferenceHelper.readCustomerPreference(this)

        if(str!=null){
            val customer = GsonHelper.stringToObject(str,Customer::class.java)
            Stocker.createInstance(customer)
            startActivity(Intent(this,CustomerActivity::class.java))
            finish()
        }
        else{
            val state= SharedPreferenceHelper.readAdminPreference(this)
            if(state){
                startActivity(Intent(this,AdminActivity::class.java))
                finish()
            }
        }

    }
}