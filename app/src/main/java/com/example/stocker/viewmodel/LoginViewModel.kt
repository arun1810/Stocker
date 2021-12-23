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

    val customerLoginStatusLiveData = MutableLiveData(State.Nothing)
    val adminLoginStatusLiveData = MutableLiveData(State.Nothing)
    var adminRepository: AdminRepository = AdminRepository(application)
    private lateinit var job:Job
    private val _result = MutableLiveData<Status>()
    val resultStatus: LiveData<Status> = _result


    fun validateCustomer(name:String,password:String){
       job =  viewModelScope.launch (Dispatchers.IO) {
           try {
               val customer = adminRepository.validateCustomer(name, password)

               if (customer != null) {
                   customerLoginStatusLiveData.postValue(State.Pass)
                   Stocker.createInstance(customer)
               } else {
                   customerLoginStatusLiveData.postValue(State.Fail)
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
                   adminLoginStatusLiveData.postValue(State.Pass)
               } else {
                   adminLoginStatusLiveData.postValue(State.Fail)
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