package com.example.stocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.helper.SortUtil
import com.example.stocker.viewmodel.helper.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

 class AdminViewModel(application: Application): AndroidViewModel(application) {

    private val adminRepository = AdminRepository(getApplication())

     private val _customersLiveData= MutableLiveData<List<Customer>>()
     val customersLiveData:LiveData<List<Customer>> = _customersLiveData
     private val _stocksLiveData = MutableLiveData<List<Stock>>()
     val stockLiveData:LiveData<List<Stock>> = _stocksLiveData
     private val _orderHistoriesLiveData = MutableLiveData<List<OrderHistory>>()
     val orderHistoriesLiveData :LiveData<List<OrderHistory>> = _orderHistoriesLiveData
     private val _result = MutableLiveData<Status>()
     val resultStatus:LiveData<Status> = _result
     private lateinit var job : Job
     private var filterOnStocks = false
     private var filterOnOrders = false
     private var filterOnCustomer = false
     val selectedCustomer = mutableListOf<Customer>()
     val customerSelectionState = MutableLiveData<Boolean>()
     val selectedOrders = mutableListOf<OrderHistory>()
     val orderSelectionState = MutableLiveData<Boolean>()
     val selectedStocks = mutableListOf<Stock>()
     val stockSelectionState = MutableLiveData<Boolean>()


    init{
        _customersLiveData.value= listOf()
        _stocksLiveData.value= listOf()
        _orderHistoriesLiveData.value= listOf()
        _result.value=Status()
        customerSelectionState.value=true
        orderSelectionState.value=true
        stockSelectionState.value=true

        getAllCustomers()
        getAllStocks()
        getAllOrderHistory()
    }


    private fun getAllCustomers(){
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                _customersLiveData.postValue(adminRepository.getAllCustomerData())
            }
            catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while retrieving customers"
                })
            }
        }
    }

    private fun getAllStocks(){
       job =  viewModelScope.launch(Dispatchers.IO) {
            try {
                _stocksLiveData.postValue(adminRepository.getAllStock())
            }catch(e:Exception){
                job.cancel()
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while retrieving stocks"
                })
            }
        }
    }

    private fun getAllOrderHistory(){
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                _orderHistoriesLiveData.postValue(adminRepository.getAllOrderHistory())
            }
            catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while retrieving stocks"
                })
            }
        }
        
    }

     fun join(lam:()->Unit){
         viewModelScope.launch(Dispatchers.Main) {

             job.join()
                if(!job.isCancelled){
                    lam()
                }
         }
     }

     fun clearStockFilter():Boolean{
         if(filterOnStocks) {
             getAllStocks()
             filterOnStocks=false
             return filterOnStocks}

         return false
     }

     fun clearCustomerFilter():Boolean{
         if(filterOnCustomer) {
             getAllCustomers()
             filterOnCustomer=false
             return filterOnCustomer}

         return false
     }

     fun clearOrderFilter():Boolean{
         if(filterOnOrders) {
             getAllOrderHistory()
             filterOnOrders=false
             return filterOnOrders
         }
         return false
     }

    fun sortOrderHistoryByTotalPrice(order:SortUtil.SortOrder){
       job =  viewModelScope.launch(Dispatchers.IO) {

            _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                adminRepository.sortOrderHistoryByTotalPrice(
                    it.toMutableList(),order)
            })
        }
    }
     fun sortOrderHistoryByDate(order:SortUtil.SortOrder){
         job =  viewModelScope.launch(Dispatchers.IO) {

             _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                 adminRepository.sortOrderHistoryByDate(
                     it.toMutableList(),order)
             })
         }
     }

    fun filterOrderHistoryByStockId(filter:String){
        job = viewModelScope.launch(Dispatchers.IO) {
            filterOnOrders=true
            _orderHistoriesLiveData.postValue(
                _orderHistoriesLiveData.value?.let{
                    adminRepository.filterOrderHistoryByStockId(it.toMutableList(),filter)
                }
            )

        }
    }

    fun sortStockByPrice(order:SortUtil.SortOrder){
      job =   viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let{
                    adminRepository.sortStockByPrice(it.toMutableList(),order)
                }
            )

        }
    }

    fun sortStockByCount(order:SortUtil.SortOrder){
       job =  viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let{
                    adminRepository.sortStockByCount(it.toMutableList(),order)
                }
            )

        }
    }

     fun sortStockByName(order:SortUtil.SortOrder){
         job =  viewModelScope.launch(Dispatchers.IO) {

             _stocksLiveData.postValue(
                 _stocksLiveData.value?.let{
                     adminRepository.sortStockByName(it.toMutableList(),order)
                 }
             )

         }
     }

    fun filterStockByName(filter:String){
      job =   viewModelScope.launch(Dispatchers.IO) {
                filterOnStocks=true
            _stocksLiveData.postValue(
                _stocksLiveData.value?.let{
                    adminRepository.filterStockByName(it.toMutableList(),filter)
                }
            )

        }
    }

    fun createNewCustomer(customer:Customer){
       job =  viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.createNewCustomer(customer)
                _customersLiveData.value?.let {
                    _customersLiveData.postValue(_customersLiveData.value!!.toMutableList().apply {
                        add(0,customer)
                    })
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while adding customer"
                })
            }
        }
    }
     fun updateCustomer(oldCustomer:Customer,newCustomer:Customer){
         job =  viewModelScope.launch(Dispatchers.IO) {
             try {
                 adminRepository.updateCustomer(newCustomer)
                 _customersLiveData.value?.let {
                     _customersLiveData.postValue(_customersLiveData.value!!.toMutableList().apply {
                         remove(oldCustomer)
                         add(0,newCustomer)
                     })
                     customerSelectionState.postValue(false)
                     selectedCustomer.clear()

                 }
             }catch(e:Exception){
                 _result.postValue(_result.value?.apply {
                     isHandled=false
                     job.cancel()
                     msg="Something went wrong while updating customer"
                 })
             }
         }

     }

    fun getCustomer(customerId:String){
     job =    viewModelScope.launch(Dispatchers.IO) {
            try {
                _customersLiveData.value?.let {
                    adminRepository.getCustomer(it.toMutableList(), customerId)
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while retrieving customer"
                })
            }
        }
    }

    fun sortCustomerByName(order:SortUtil.SortOrder){
       job =  viewModelScope.launch(Dispatchers.IO) {
            _customersLiveData.postValue(
                _customersLiveData.value?.let {
                    adminRepository.sortCustomerByName(it.toMutableList(),order)
                }
            )
        }
    }

    fun filterCustomerByName(filter:String){
       job =  viewModelScope.launch(Dispatchers.IO) {
           filterOnCustomer = true
            _customersLiveData.postValue(
                _customersLiveData.value?.let {
                    adminRepository.filterCustomerByName(it.toMutableList(),filter)
                }
            )
        }
    }

    fun addStock(stock: Stock){
       job =  viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.addStock(stock)
                _stocksLiveData.value?.let {
                    _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                        add(0,stock)
                    })
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while adding stock"
                })
            }
        }
    }

    fun updateStock(oldStock:Stock,newStock:Stock){
      job =   viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.updateStock(newStock)
                _stocksLiveData.value?.let {
                    _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                        remove(oldStock)
                        add(0,newStock)
                    })
                    stockSelectionState.postValue(false)
                    selectedStocks.clear()

                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while updating stock"
                })
            }
        }
    }

    fun buyStock(stockId:String,count:Int){
       job =  viewModelScope.launch(Dispatchers.IO) {
            val stock = findStockFromList(stockId) ?: throw Exception()
            val updatedStock = Stock(stockID =stock.stockID, stockName = stock.stockName,count = count, price = stock.price, discount = stock.discount)
            adminRepository.updateStock(updatedStock)
            updateStockInList(updatedStock)
        }
    }

    fun removeOrderHistory(){
      job =   viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.removeOrderHistory(selectedOrders)
                _orderHistoriesLiveData.value?.let { list ->
                    val mutableList = list.toMutableList()
                    for (order in selectedOrders) {
                        for (selectedOrderHistory in mutableList) {
                            if (order.orderID == selectedOrderHistory.orderID) {
                                mutableList.remove(order)
                                break
                            }
                        }
                    }
                    selectedOrders.clear()
                    orderSelectionState.postValue(true)
                    _orderHistoriesLiveData.postValue(mutableList)
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while removing orderHistory"
                })
            }
        }
    }

    fun removeStock(){
       job =  viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.removeStock(selectedStocks)
                _stocksLiveData.value?.let { list ->
                    val mutableList = list.toMutableList()
                    for (stock in selectedStocks) {
                        for (selectedStockItem in mutableList) {
                            if (stock.stockID == selectedStockItem.stockID) {
                                mutableList.remove(stock)
                                break
                            }
                        }
                    }
                    selectedStocks.clear()
                    stockSelectionState.postValue(true)
                    _stocksLiveData.postValue(mutableList)
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while removing stock"
                })
            }
        }
    }

    fun removeCustomer(){
       job =  viewModelScope.launch(Dispatchers.IO) {
            try {
                if(adminRepository.removeCustomer(selectedCustomer)) {
                    _customersLiveData.value?.let { list ->
                        val mutableList = list.toMutableList()
                        for (customer in selectedCustomer) {
                            for (cus in mutableList) {
                                if (cus.customerId == customer.customerId) {
                                    mutableList.remove(cus)
                                    break
                                }
                            }
                        }
                        selectedCustomer.clear()
                        customerSelectionState.postValue(true)
                        _customersLiveData.postValue(mutableList)
                    }
                }
                else{
                    throw java.lang.Exception("something went wrong")
                }
            }catch(e:Exception){
                _result.postValue(_result.value?.apply {
                    isHandled=false
                    job.cancel()
                    msg="Something went wrong while removing stock"
                })
            }
        }

    }

    private fun findStockFromList(stockId:String):Stock?{
       return  _stocksLiveData.value?.let{
            return@let it.find { stock->stock.stockID == stockId }
        }
    }
    private fun deleteStockFromList(list:MutableList<Stock>,stock:Stock){
        list.dropWhile { it.stockID == stock.stockID }
    }

    private fun updateStockInList(stock:Stock):List<Stock>?{

       return  _stocksLiveData.value?.let { list ->
            list.toMutableList().apply{
                deleteStockFromList(this,stock)
                this.add(0,stock)
            }
        }
    }
}