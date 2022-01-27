package com.example.stocker.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.fragments.util.GsonHelper
import com.example.stocker.view.fragments.util.SharedPreferenceHelper

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