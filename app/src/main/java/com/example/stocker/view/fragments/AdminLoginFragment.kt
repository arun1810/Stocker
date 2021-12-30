package com.example.stocker.view.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.stocker.R
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.viewmodel.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class AdminLoginFragment : Fragment() {

    private val model by activityViewModels<LoginViewModel>()
    private lateinit var navController:NavController

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         activity?.requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return inflater.inflate(R.layout.fragment_admin_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title: MaterialTextView = view.findViewById(R.id.title)
        val s="Hello\nLogin to continue"
        val spannable = SpannableString(s)
        spannable.setSpan(RelativeSizeSpan(2f),0,5,0)
        spannable.setSpan(ForegroundColorSpan(Color.argb(180,254,254,254)),6,23,0)
        title.text=spannable

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController  = navHost.navController

        val passwordLayout = view.findViewById<TextInputLayout>(R.id.admin_login_password_layout)
        val passwordEtx = view.findViewById<TextInputEditText>(R.id.admin_login_password_Etx)

        passwordEtx.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(passwordLayout.error!=null) passwordLayout.error=null
            }

        })

        val loginBtn = view.findViewById<MaterialButton>(R.id.admin_login_btn)

        loginBtn.setOnClickListener {
            val password = passwordEtx.text.toString()
            if(password.isEmpty()){
                passwordLayout.error = "password cannot be empty"
            }
            else {
                model.validateAdmin(password)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        model.adminLoginStatusLiveData.observe(this,{state->
            when(state){
                LoginViewModel.State.Pass->{
                    SharedPreferenceHelper.writeAdminPreference(activity!!,true)

                    navController.navigate(R.id.action_adminLoginFragment_to_adminActivity)
                    activity!!.finish()
                }
                LoginViewModel.State.Fail->{
                    val snackbar = Snackbar.make(view!!, "invalid password", Snackbar.LENGTH_LONG)
                    snackbar.setAction("ok") {
                        snackbar.dismiss()
                    }
                    snackbar.show()
                }
                LoginViewModel.State.Nothing->{
                    //do nothing
                }
            }
        })

        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    val snackbar = Snackbar.make(view!!, status.msg, Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction("close") {
                        status.isHandled = true
                        snackbar.dismiss()
                    }.show()
                }
            }

        })
    }
}