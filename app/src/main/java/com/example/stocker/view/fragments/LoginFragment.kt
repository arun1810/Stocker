package com.example.stocker.view.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.stocker.R
import com.google.android.material.button.MaterialButton

class LoginFragment : Fragment() {

 override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adminLoginBtn = view.findViewById<MaterialButton>(R.id.admin_login)
        val customerLoginBtn = view.findViewById<MaterialButton>(R.id.customer_login)

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController  = navHost.navController

        adminLoginBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_adminLoginFragment)
        }

        customerLoginBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_customerLoginFragment)
        }
    }
}