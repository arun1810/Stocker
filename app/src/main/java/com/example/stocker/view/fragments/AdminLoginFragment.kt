package com.example.stocker.view.fragments

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.stocker.R
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.viewmodel.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
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
        spannable.setSpan(ForegroundColorSpan(
            MaterialColors.getColor(context!!,android.R.attr.textColor,context!!.getColor(R.color.darkPrimaryTextColor))),6,23,0)
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

                when (state.state) {
                    LoginViewModel.State.Pass -> {
                        if(! state.isHandled) {
                            SharedPreferenceHelper.writeAdminPreference(activity!!, true)

                            navController.navigate(R.id.action_adminLoginFragment_to_adminActivity)
                            activity!!.finish()
                        }
                    }
                    LoginViewModel.State.Fail -> {
                        if(! state.isHandled) {
                            val snackBar =
                                Snackbar.make(view!!, "invalid password", Snackbar.LENGTH_LONG)
                            snackBar.show()
                        }
                    }
                    LoginViewModel.State.Nothing -> {
                        //do nothing
                    }
                }
            state.isHandled=true
            state.state=LoginViewModel.State.Nothing

        })

        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    status.isHandled = true
                    val snackBar = Snackbar.make(view!!, status.msg, Snackbar.LENGTH_INDEFINITE)
                    .show()
                }
            }

        })
    }
}