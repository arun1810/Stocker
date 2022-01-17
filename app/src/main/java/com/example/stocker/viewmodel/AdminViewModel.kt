package com.example.stocker.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.helper.SortUtil
import com.example.stocker.view.fragments.util.Type
import com.example.stocker.viewmodel.helper.*
import kotlinx.coroutines.*
import java.util.regex.Pattern

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val adminRepository = AdminRepository(getApplication())

    private var originalStocks = mutableListOf<Stock>()
    private var lastStockQuery=""
    private var originalCustomers = mutableListOf<Customer>()
    private var lastCustomerQuery=""
    private var originalOrderHistories = mutableListOf<OrderHistory>()
    private var lastOrderHistoryQuery=""


    private val _customersLiveData = MutableLiveData<List<Customer>>(listOf())
    val customersLiveData: LiveData<List<Customer>> = _customersLiveData

    private val _stocksLiveData = MutableLiveData<List<Stock>>(listOf())
    val stockLiveData: LiveData<List<Stock>> = _stocksLiveData

    private val _orderHistoriesLiveData = MutableLiveData<List<OrderHistory>>(listOf())
    val orderHistoriesLiveData: LiveData<List<OrderHistory>> = _orderHistoriesLiveData

    private val _stockError = MutableLiveData(Error())
    val stockErrorStatus: LiveData<Error> = _stockError

    private val _customerError = MutableLiveData(Error())
    val customerErrorStatus: LiveData<Error> = _customerError

    private val _orderError = MutableLiveData(Error())
    val orderErrorStatus: LiveData<Error> = _orderError

    private val _stockDetailGetterError = MutableLiveData(Error())
    val stockDetailsGetterError: LiveData<Error> = _stockDetailGetterError

    private val _customerDetailsGetterError = MutableLiveData(Error())
    val customerDetailsGetterError: LiveData<Error> = _customerDetailsGetterError

    private lateinit var job: Job

    private var filterOnStocks = false
    private var filterOnOrders = false
    private var filterOnCustomer = false


    val selectedCustomer = mutableListOf<Customer>()
    val selectedStocks = mutableListOf<Stock>()

    private val _customerSelectionState = MutableLiveData(Type.Nothing)
    val customerSelectionState: LiveData<Type> = _customerSelectionState

    private val _stockSelectionState = MutableLiveData(Type.Nothing)
    val stockSelectionState: LiveData<Type> = _stockSelectionState


    init {
        println("admin viewModel created")

        getAllCustomers()
        getAllStocks()
        getAllOrderHistory()
    }


    fun getAllCustomers() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = adminRepository.getAllCustomerData()
                originalCustomers=data.toMutableList()
                _customersLiveData.postValue(data)
            } catch (e: Exception) {
                _customerError.postValue(_customerError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = cantRetrieveData
                })
            }
        }
    }

    fun getAllStocks() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = adminRepository.getAllStock()
                originalStocks = data.toMutableList()
                _stocksLiveData.postValue(data)
            } catch (e: Exception) {
                job.cancel()
                _stockError.postValue(_stockError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = cantRetrieveData
                })
            }
        }
    }

    fun getAllOrderHistory() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = adminRepository.getAllOrderHistory()
                originalOrderHistories = data.toMutableList()
                _orderHistoriesLiveData.postValue(data)
            } catch (e: Exception) {
                _orderError.postValue(_orderError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = cantRetrieveData
                })
            }
        }

    }

    fun join(lam: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {

            job.join()
            if (!job.isCancelled) {
                lam()
            }
        }
    }

    fun clearStockFilter(): Boolean {
        if (filterOnStocks) {
            getAllStocks()
            filterOnStocks = false
            return filterOnStocks
        }

        return false
    }

    fun clearCustomerFilter(): Boolean {
        if (filterOnCustomer) {
            getAllCustomers()
            filterOnCustomer = false
            return filterOnCustomer
        }

        return false
    }

    fun clearOrderFilter(): Boolean {
        if (filterOnOrders) {
            getAllOrderHistory()
            filterOnOrders = false
            return filterOnOrders
        }
        return false
    }

    fun sortOrderHistoryByTotalPrice(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                adminRepository.sortOrderHistoryByTotalPrice(
                    it.toMutableList(), order
                )
            })
        }
    }

    fun sortOrderHistoryByDate(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                adminRepository.sortOrderHistoryByDate(
                    it.toMutableList(), order
                )
            })
        }
    }

    fun filterOrderHistoryByStockId(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            filterOnOrders = true
            lastOrderHistoryQuery=filter

            if(filter.isEmpty()){
                _orderHistoriesLiveData.postValue(
                    originalOrderHistories
                )
            }
            else{
                _orderHistoriesLiveData.postValue(adminRepository.filterOrderHistoryByStockId(originalOrderHistories, filter))
            }


        }
    }

    fun sortStockByPrice(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let {
                    adminRepository.sortStockByPrice(it.toMutableList(), order)
                }
            )

        }
    }

    fun sortStockByCount(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let {
                    adminRepository.sortStockByCount(it.toMutableList(), order)
                }
            )

        }
    }

    fun sortStockByName(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let {
                    adminRepository.sortStockByName(it.toMutableList(), order)
                }
            )

        }
    }

    fun filterStockByName(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            lastStockQuery=filter
            filterOnStocks = true
            if(filter.isEmpty()){
                _stocksLiveData.postValue(
                    originalStocks
                )
            }
            else{
                val data = adminRepository.filterStockByName(originalStocks, filter)
                _stocksLiveData.postValue(
                    data
                )
            }


        }
    }
    fun getStock(stockId:String):Deferred<Stock?>{
        return viewModelScope.async (Dispatchers.IO) {
           _stocksLiveData.value?.find { stock -> stock.stockID==stockId }

        }

    }

    fun createNewCustomer(customer: Customer) {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.createNewCustomer(customer)
                originalCustomers.add(0,customer)
                val pattern = Pattern.compile("(.*?)$lastStockQuery(.*?)")
                if (pattern.matcher(customer.name).matches()){
                    _customersLiveData.value?.let {
                        _customersLiveData.postValue(_customersLiveData.value!!.toMutableList().apply {
                            add(0, customer)
                        })
                    }
                }

            } catch (e: Exception) {
                _customerDetailsGetterError.postValue(_customerDetailsGetterError.value?.apply {
                    isHandled = false
                    job.cancel()

                    msg = when (e) {
                        is SQLiteConstraintException -> {
                            uniqueIdError
                        }
                        else -> {
                            otherError
                        }
                    }
                })
            }
        }
    }

    fun updateCustomer(oldCustomer: Customer, newCustomer: Customer) {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.updateCustomer(newCustomer,oldCustomer.customerId)
                _customersLiveData.value?.let {
                    _customersLiveData.postValue(_customersLiveData.value!!.toMutableList().apply {
                        remove(oldCustomer)
                        add(0, newCustomer)
                    })
                    _customerSelectionState.postValue(Type.Update)
                    selectedCustomer.clear()

                }
            } catch (e: Exception) {
                _customerDetailsGetterError.postValue(_customerError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = when (e) {
                        is SQLiteConstraintException -> {
                            uniqueIdError
                        }
                        else -> {
                            otherError
                        }
                    }
                })
            }
        }

    }


    fun sortCustomerByName(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {
            _customersLiveData.postValue(
                _customersLiveData.value?.let {
                    adminRepository.sortCustomerByName(it.toMutableList(), order)
                }
            )
        }
    }

    fun filterCustomerByName(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            lastCustomerQuery=filter
            filterOnCustomer = true
            if(filter.isEmpty()){
                _customersLiveData.postValue(originalCustomers)
            }
            else{
                _customersLiveData.postValue(
                        adminRepository.filterCustomerByName(originalCustomers, filter)
                )
            }

        }
    }

    fun addStock(stock: Stock) {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.addStock(stock)
                _stocksLiveData.value?.let {
                    originalStocks.add(0,stock)
                    val pattern = Pattern.compile("(.*?)$lastStockQuery(.*?)")

                    if(pattern.matcher(stock.stockName).matches()) {
                        _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                            add(0, stock)
                        })
                    }
                }
            } catch (e: Exception) {
                _stockDetailGetterError.postValue(_stockDetailGetterError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = when (e) {
                        is SQLiteConstraintException -> {
                            uniqueIdError
                        }
                        else -> {
                            otherError
                        }
                    }
                })
            }
        }
    }

    fun updateStock(oldStock: Stock, newStock: Stock) {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.updateStock(newStock,oldStock.stockID)
                _stocksLiveData.value?.let {
                    _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                        remove(oldStock)
                        add(0, newStock)
                    })
                    selectedStocks.clear()
                    _stockSelectionState.postValue(Type.Update)


                }
            } catch (e: Exception) {
                _stockDetailGetterError.postValue(_stockError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = when (e) {
                        is SQLiteConstraintException -> {
                            uniqueIdError
                        }
                        else -> {
                            otherError
                        }
                    }
                })
            }
        }
    }

/*
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

 */

    fun removeStock() {
        job = viewModelScope.launch(Dispatchers.IO) {
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
                    _stockSelectionState.postValue(Type.Delete)
                    _stocksLiveData.postValue(mutableList)
                }
            } catch (e: Exception) {
                _stockError.postValue(_stockError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = deleteError
                })
            }
        }
    }

    fun removeCustomer() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                if (adminRepository.removeCustomer(selectedCustomer)) {
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
                        _customerSelectionState.postValue(Type.Delete)
                        _customersLiveData.postValue(mutableList)
                    }
                } else {
                    throw java.lang.Exception("something went wrong")
                }
            } catch (e: Exception) {
                _customerError.postValue(_customerError.value?.apply {
                    isHandled = false
                    job.cancel()
                    msg = deleteError
                })
            }
        }

    }
}