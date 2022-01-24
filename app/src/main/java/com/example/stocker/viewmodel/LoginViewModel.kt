package com.example.stocker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.stocker.pojo.Stocker
import com.example.stocker.repository.AdminRepository
import com.example.stocker.viewmodel.helper.Error
import com.example.stocker.viewmodel.helper.validationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginResult{
    var state: LoginViewModel.State =LoginViewModel.State.Nothing
    var isHandled:Boolean=true
}

class LoginViewModel(application: Application): AndroidViewModel(application) {
    enum class State{Pass,Fail,Nothing}

    private val _customerLoginStatusLiveData = MutableLiveData(LoginResult())
    val customerLoginStatusLiveData: LiveData<LoginResult> = _customerLoginStatusLiveData

    private val _adminLoginStatusLiveData = MutableLiveData(LoginResult())
    val adminLoginStatusLiveData:LiveData<LoginResult> = _adminLoginStatusLiveData

    private var adminRepository: AdminRepository = AdminRepository(application)
    private lateinit var job:Job
    private val _result = MutableLiveData(Error())
    val resultStatus: LiveData<Error> = _result

    init {
        println("login viewModel created")
    }


    fun validateCustomer(name:String,password:String){
       job =  viewModelScope.launch (Dispatchers.IO) {
           try {
               val customer = adminRepository.validateCustomer(name, password)

               if (customer != null) {
                   Stocker.createInstance(customer)
                   _customerLoginStatusLiveData.postValue(_customerLoginStatusLiveData.value?.apply {
                       isHandled=false
                       state=State.Pass
                   })

               } else {
                   _customerLoginStatusLiveData.postValue(_customerLoginStatusLiveData.value?.apply {
                       isHandled=false
                       state=State.Fail
                   })
               }
           }
           catch (e:Exception){
               _result.postValue(_result.value?.apply {
                   job.cancel()
                   isHandled=false
                   job.cancel()
                   msg= validationError
               })
           }
       }
    }

    fun validateAdmin(password:String) {
       job =  viewModelScope.launch(Dispatchers.IO) {
           try {
               if (adminRepository.validateAdmin(password)) {
                   _adminLoginStatusLiveData.postValue(
                       _adminLoginStatusLiveData.value?.apply {
                           isHandled=false
                           state=State.Pass
                       }
                   )
               } else {
                   _adminLoginStatusLiveData.postValue(_adminLoginStatusLiveData.value?.apply {
                       isHandled=false
                       state=State.Fail
                   })
               }
           }catch(e:Exception){
               _result.postValue(_result.value?.apply {
                   job.cancel()
                   isHandled=false
                   job.cancel()
                   msg= validationError
               })
           }
        }
    }

    fun join(lam:()->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            job.join()
            lam()
        }

    }
}