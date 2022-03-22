package com.example.stocker.viewmodel.helper

import okhttp3.OkHttpClient
import okhttp3.Request

object IdGenerator {



    fun generateId():String?{
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://randommer.io/api/Text/Password?length=5&hasDigits=true&hasUppercase=true&hasSpecial=false")
            .header("X-Api-Key","dea19a8e7c004f15be79911522cb89dc")
            .build()

        try{
           val response= client.newCall(request).execute()
            var id = response.body!!.string()
            id = id.subSequence(1,id.length-1).toString()
            return id
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
}