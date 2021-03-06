package com.example.stocker.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocker.di.componants.AdminRepoComponent
import com.example.stocker.di.componants.CustomerRepoComponent
import com.example.stocker.di.componants.DaggerAdminRepoComponent
import com.example.stocker.di.componants.DaggerCustomerRepoComponent
import com.example.stocker.di.module.AdminRepoModule
import com.example.stocker.di.module.CustomerRepoModule
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.repository.AdminRepository
import com.example.stocker.repository.baseinterface.BaseAdminRepository
import com.example.stocker.repository.helper.SortUtil
import com.example.stocker.view.fragments.util.Type
import com.example.stocker.viewmodel.helper.*
import kotlinx.coroutines.*
import java.util.regex.Pattern
import javax.inject.Inject

class AdminViewModel(application: Application) : AndroidViewModel(application) {


    lateinit var adminRepoComponent: AdminRepoComponent
    @Inject
    lateinit var adminRepository:BaseAdminRepository // = AdminRepository()
       // BaseAdminRepository(getApplication())

    private var originalStocks = mutableListOf<Stock>()
    private var lastStockQuery = ""
    private var originalCustomers = mutableListOf<Customer>()
    private var lastCustomerQuery = ""

    private var originalOrderHistories = mutableListOf<OrderHistory>()


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

        adminRepoComponent = DaggerAdminRepoComponent.builder().adminRepoModule(AdminRepoModule(application)).build()
        adminRepoComponent.inject(this)

        getAllCustomers()
        getAllStocks()
        getAllOrderHistory()

       // adminRepoComponent =
    }


    fun getAllCustomers() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = adminRepository.getAllCustomerData()
                originalCustomers = data.toMutableList()
                _customersLiveData.postValue(mutableListOf<Customer>().apply { addAll(data) })
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
                _stocksLiveData.postValue(mutableListOf<Stock>().apply {
                    addAll(data)
                })
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
                _orderHistoriesLiveData.postValue(mutableListOf<OrderHistory>().apply { addAll(data) })
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

    /*
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

     */

    fun sortOrderHistoryByTotalPrice(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                adminRepository.sortOrderHistoryByTotalPrice(
                    it.toMutableList(), order
                )
            })
            originalOrderHistories = adminRepository.sortOrderHistoryByTotalPrice(
                originalOrderHistories, order
            ).toMutableList()
        }
    }

    fun sortOrderHistoryByDate(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _orderHistoriesLiveData.postValue(_orderHistoriesLiveData.value?.let {
                adminRepository.sortOrderHistoryByDate(
                    it.toMutableList(), order
                )
            })

            originalOrderHistories = adminRepository.sortOrderHistoryByDate(
                originalOrderHistories, order
            ).toMutableList()

        }
    }

    fun filterOrderHistoryByStockId(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            filterOnOrders = true

            if (filter.isEmpty()) {
                _orderHistoriesLiveData.postValue(
                    mutableListOf<OrderHistory>().apply {
                        addAll(originalOrderHistories)
                    }
                )
            } else {
                _orderHistoriesLiveData.postValue(
                    adminRepository.filterOrderHistoryByStockId(
                        originalOrderHistories,
                        filter
                    )
                )
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
            originalStocks = adminRepository.sortStockByPrice(originalStocks, order)

        }
    }

    fun sortStockByCount(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let {
                    adminRepository.sortStockByCount(it.toMutableList(), order)
                }
            )

            originalStocks = adminRepository.sortStockByCount(originalStocks, order)

        }
    }

    fun sortStockByName(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.postValue(
                _stocksLiveData.value?.let {
                    adminRepository.sortStockByName(it.toMutableList(), order)
                }
            )

            originalStocks = adminRepository.sortStockByName(originalStocks, order)

        }
    }

    fun filterStockByName(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            lastStockQuery = filter
            filterOnStocks = true
            if (filter.isEmpty()) {
                _stocksLiveData.postValue(
                    mutableListOf<Stock>().apply {
                        addAll(originalStocks)
                    }
                )
            } else {
                val data = adminRepository.filterStockByName(originalStocks, filter)
                _stocksLiveData.postValue(
                    data
                )
            }


        }
    }

    suspend fun getStock(stockId: String): Stock? {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            originalStocks.find { stock -> stock.stockID == stockId }

        }

    }

    fun createNewCustomer(customer: Customer) {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                adminRepository.createNewCustomer(customer)
                originalCustomers.add(0, customer)
                val pattern = Pattern.compile("(.*?)${lastCustomerQuery}(.*?)")
                if (pattern.matcher(customer.name).matches()) {
                    _customersLiveData.value?.let {
                        _customersLiveData.postValue(
                            _customersLiveData.value!!.toMutableList().apply {
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
                adminRepository.updateCustomer(newCustomer, oldCustomer.customerId)
                _customersLiveData.value?.let {
                    _customersLiveData.postValue(_customersLiveData.value!!.toMutableList().apply {
                        remove(oldCustomer)
                        add(0, newCustomer)
                        originalCustomers.remove(oldCustomer)
                        originalCustomers.add(0, newCustomer)
                    })
                    clearCustomerSelection(Type.Update)

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

            originalCustomers =
                adminRepository.sortCustomerByName(originalCustomers, order).toMutableList()
        }
    }

    fun sortCustomerByDOB(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {
            _customersLiveData.postValue(
                _customersLiveData.value?.let {
                    adminRepository.sortCustomerByDOB(it.toMutableList(), order)
                }
            )

            originalCustomers =
                adminRepository.sortCustomerByDOB(originalCustomers, order).toMutableList()
        }
    }

    fun filterCustomerByName(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            lastCustomerQuery = filter
            filterOnCustomer = true
            if (filter.isEmpty()) {
                _customersLiveData.postValue(mutableListOf<Customer>().apply {
                    addAll(
                        originalCustomers
                    )
                })
            } else {
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
                originalStocks.add(0, stock)
                _stocksLiveData.value?.let {

                    val pattern = Pattern.compile("(.*?)$lastStockQuery(.*?)")

                    if (pattern.matcher(stock.stockName).matches()) {
                        _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                            add(0, stock)
                        })
                    }

                }
                println("finished")
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
                adminRepository.updateStock(newStock, oldStock.stockID)
                _stocksLiveData.value?.let {
                    _stocksLiveData.postValue(_stocksLiveData.value!!.toMutableList().apply {
                        remove(oldStock)
                        add(0, newStock)
                        originalStocks.remove(oldStock)
                        originalStocks.add(newStock)
                    })
                    clearStockSelection(Type.Update)


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
                                originalStocks.remove(stock)
                                break
                            }
                        }
                    }
                    clearStockSelection(Type.Delete)
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
                                    originalCustomers.remove(cus)
                                    break
                                }
                            }
                        }
                        clearCustomerSelection(Type.Delete)

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

    fun clearStockSelection(type: Type) {
        selectedStocks.clear()
        _stockSelectionState.postValue(type)
    }

    fun clearCustomerSelection(type: Type) {
        selectedCustomer.clear()
        _customerSelectionState.postValue(type)
    }
}