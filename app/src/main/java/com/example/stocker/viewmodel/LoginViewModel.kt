package com.example.stocker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.stocker.pojo.Stocker
import com.example.stocker.repository.AdminRepository
import com.example.stocker.viewmodel.helper.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {
    enum class State{Pass,Fail,Nothing}

    private val _customerLoginStatusLiveData = MutableLiveData(State.Nothing)
    val customerLoginStatusLiveData: LiveData<State> = _customerLoginStatusLiveData
    private val _adminLoginStatusLiveData = MutableLiveData(State.Nothing)
    val adminLoginStatusLiveData:LiveData<State> = _adminLoginStatusLiveData
    private var adminRepository: AdminRepository = AdminRepository(application)
    private lateinit var job:Job
    private val _result = MutableLiveData<Status>()
    val resultStatus: LiveData<Status> = _result

    init {
        println("login viewModel created")
    }


    fun validateCustomer(name:String,password:String){
       job =  viewModelScope.launch (Dispatchers.IO) {
           try {
               val customer = adminRepository.validateCustomer(name, password)

               if (customer != null) {
                   _customerLoginStatusLiveData.postValue(State.Pass)
                   Stocker.createInstance(customer)
               } else {
                   _customerLoginStatusLiveData.postValue(State.Fail)
               }
           }
           catch (e:Exception){
               _result.postValue(_result.value?.apply {
                   job.cancel()
                   isHandled=false
                   job.cancel()
                   msg="Something went wrong"
               })
           }
       }
    }

    fun validateAdmin(password:String) {
       job =  viewModelScope.launch(Dispatchers.IO) {
           try {
               if (adminRepository.validateAdmin(password)) {
                   _adminLoginStatusLiveData.postValue(State.Pass)
               } else {
                   _adminLoginStatusLiveData.postValue(State.Fail)
               }
           }catch(e:Exception){
               _result.postValue(_result.value?.apply {
                   job.cancel()
                   isHandled=false
                   job.cancel()
                   msg="Something went wrong"
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