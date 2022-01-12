package com.example.stocker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stock
import com.example.stocker.pojo.StockInCart
import com.example.stocker.pojo.Stocker
import com.example.stocker.repository.CustomerRepository
import com.example.stocker.repository.helper.SortUtil
import com.example.stocker.viewmodel.helper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class CustomerViewModel(application: Application):AndroidViewModel(application) {



    private val _stocksLiveData = MutableLiveData<List<Stock>>(listOf())
    val stocksLiveData:LiveData<List<Stock>> = _stocksLiveData
    private val _orderHistoryLiveData = MutableLiveData<List<OrderHistory>>(listOf())
    val orderHistoryLiveData:LiveData<List<OrderHistory>> = _orderHistoryLiveData

    private val _stockErrorStatus = MutableLiveData(Error())
    val stockErrorStatus:LiveData<Error> = _stockErrorStatus

    private val _orderHistoryErrorStatus = MutableLiveData(Error())
    val orderHistoryErrorStatus:LiveData<Error> = _orderHistoryErrorStatus

    private val _cartErrorStatus=MutableLiveData(Error())
    val cartErrorStatus:LiveData<Error> =_cartErrorStatus

    val selectedArray:HashMap<Stock,Int> =HashMap()
    private val customerRepository = CustomerRepository(application,Stocker.getInstance()!!.customer!!.customerId)
    private var filterOnStocks = false
    private var filterOnOrders = false
    private lateinit var job :Job
    private lateinit var selectedStockIds:Array<String>

    init {

        getAllStocks()
        getAllOrderHistory()

       println("customer viewModel created")

    }

     fun getAllStocks(){
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                _stocksLiveData.postValue(customerRepository.getAllStocks())
            }
            catch (e:Exception){
                _stockErrorStatus.postValue(_stockErrorStatus.value?.apply {
                    job.cancel()
                    isHandled=false
                    msg= cantRetrieveData
                })
            }
        }
    }

    fun getAllOrderHistory(){
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                _orderHistoryLiveData.postValue(customerRepository.getAllOrderHistory())
            }catch(e:Exception){
                e.printStackTrace()
                _orderHistoryErrorStatus.postValue(_orderHistoryErrorStatus.value?.apply {
                    job.cancel()
                    isHandled=false
                    msg= cantRetrieveData
                })
            }
        }
    }

    suspend fun calcCart(): StockInCart {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val selectedStocks = selectedArray.keys.toTypedArray()
            val selectedStockCount = selectedArray.values.toTypedArray()
            val prices = Array(selectedStocks.size) { "" }
            val stockNames = Array(selectedStocks.size) { "" }
            var total=0
            selectedStockIds = Array(selectedStocks.size) { "" }


            for (i in selectedStocks.indices) {
                val stock = selectedStocks[i]
                stockNames[i] = stock.stockName
                selectedStockIds[i] = stock.stockID
                val price = calcPrice(stock.price, stock.discount,selectedStockCount[i])
                prices[i] = price.toString()
                total += price
            }
            StockInCart(stockNames, selectedStockIds, selectedStockCount, prices, total)


        }

    }

    suspend fun placeOrder(total: Int): Boolean {

        return withContext(viewModelScope.coroutineContext + Dispatchers.IO ) {
            val id = IdGenerator.generateId() ?: return@withContext  false

            val result = customerRepository.placeOrder(
                id ,
                selectedArray,
                selectedStockIds,
                selectedArray.values.toTypedArray(),
                total
            )
            if (result.first) {
                updatePurchase(result.second)
                clearSelection()
                true
            } else {
                false
            }
        }
    }

    fun sortOrderHistoryByTotalPrice(order: SortUtil.SortOrder){
        job = viewModelScope.launch(Dispatchers.IO) {
            _orderHistoryLiveData.value?.let{
                _orderHistoryLiveData.postValue(  customerRepository.sortOrderHistoryByTotalPrice(it.toMutableList(),order))
            }
        }

    }
    fun sortOrderHistoryByDate(order: SortUtil.SortOrder){
        job = viewModelScope.launch(Dispatchers.IO) {
            _orderHistoryLiveData.value?.let{
                _orderHistoryLiveData.postValue(  customerRepository.sortOrderHistoryByDate(it.toMutableList(),order))
            }
        }
    }
    fun filterOrderHistoryByStockId(filter:String){

        job = viewModelScope.launch(Dispatchers.IO) {
            _orderHistoryLiveData.value?.let{
                _orderHistoryLiveData.postValue( customerRepository.filterOrderHistoryByStockId(it.toMutableList(),filter))
                filterOnOrders=true
            }
        }
    }

    fun sortStockByPrice(order: SortUtil.SortOrder){

        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let{
                _stocksLiveData.postValue( customerRepository.sortStockByPrice(it.toMutableList(),order))

            }
        }

    }

    fun sortStockByCount(order: SortUtil.SortOrder){
        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let{
                _stocksLiveData.postValue( customerRepository.sortStockByCount(it.toMutableList(),order))
            }
        }
    }

    fun sortStockByName(order: SortUtil.SortOrder){
        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let{
                _stocksLiveData.postValue(customerRepository.sortStockByName(it.toMutableList(),order))
            }
        }
    }

    fun filterStockByName(filter:String){
        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let{
                filterOnStocks=true
                _stocksLiveData.postValue( customerRepository.filterStockByName(it.toMutableList(),filter))
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

    fun clearOrderFilter():Boolean{
        if(filterOnOrders) {
            getAllOrderHistory()
            filterOnOrders=false
            return filterOnOrders
        }
        return false
    }

    fun join(lam:()->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            job.join()
            if(!job.isCancelled){
                lam()
            }

        }
    }

    private fun calcPrice(price: Int, discount: Int, count: Int): Int {
        val total = price * count
        return (total - (total * (discount / 100f)).roundToInt())
    }

    private fun clearSelection(){
        selectedArray.clear()
        selectedStockIds= arrayOf("")

    }

    private fun updatePurchase(newOrder:OrderHistory){
            _orderHistoryLiveData.value?.let{
                try {
                    val newData =
                        _orderHistoryLiveData.value!!.toMutableList().apply { add(newOrder) }
                    _orderHistoryLiveData.postValue(newData)
                }
                catch(e:Exception){
                    _cartErrorStatus.postValue(_cartErrorStatus.value?.apply {
                        isHandled=false
                        msg= otherError
                    })
                }
            }
    }
}