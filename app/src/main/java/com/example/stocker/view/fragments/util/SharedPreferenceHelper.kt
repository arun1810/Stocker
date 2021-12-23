package com.example.stocker.view.fragments.util

import android.app.Activity
import android.content.Context

class SharedPreferenceHelper {

    companion object{
        fun writeCustomerPreference(activity: Activity, msg: String?){
            activity.getSharedPreferences("LoginCustomerObj", Context.MODE_PRIVATE).edit().putString("CustomerObj",msg).apply()
        }

        fun readCustomerPreference(activity: Activity):String?{
             return activity.getSharedPreferences("LoginCustomerObj", Context.MODE_PRIVATE).getString("CustomerObj",null)
        }

        fun writeAdminPreference(activity: Activity,state:Boolean){
            activity.getSharedPreferences("LoginAdminState", Context.MODE_PRIVATE).edit().putBoolean("AdminState",state).apply()
        }

        fun readAdminPreference(activity: Activity):Boolean{
            return activity.getSharedPreferences("LoginAdminState", Context.MODE_PRIVATE).getBoolean("AdminState",false)
        }
    }
}