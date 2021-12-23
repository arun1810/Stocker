package com.example.stocker.view.fragments.util

import com.google.gson.Gson

class GsonHelper {

    companion object{
        private val gson = Gson()

        fun <T>stringToObject(data:String,type:Class<T>)=gson.fromJson(data,type)

        fun <T>objectToString(data:T) = gson.toJson(data)
    }
}