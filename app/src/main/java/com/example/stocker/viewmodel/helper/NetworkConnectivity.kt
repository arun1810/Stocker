package com.example.stocker.viewmodel.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService

object NetworkConnectivity {
    fun isNetworkAvailable(context: Context):Boolean{
        val connectivityManager = getSystemService(context,ConnectivityManager::class.java)!!
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return  networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}