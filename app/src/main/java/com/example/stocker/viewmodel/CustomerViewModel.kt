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

class CustomerViewModel(application: Application) : AndroidViewModel(application) {


    private var originalStocks = mutableListOf<Stock>()
    private var originalOrderHistories = mutableListOf<OrderHistory>()


    private val _stocksLiveData = MutableLiveData<List<Stock>>(listOf())
    val stocksLiveData: LiveData<List<Stock>> = _stocksLiveData

    private val _orderHistoryLiveData = MutableLiveData<List<OrderHistory>>(listOf())
    val orderHistoryLiveData: LiveData<List<OrderHistory>> = _orderHistoryLiveData

    private val _stockErrorStatus = MutableLiveData(Error())
    val stockErrorStatus: LiveData<Error> = _stockErrorStatus

    private val _orderHistoryErrorStatus = MutableLiveData(Error())
    val orderHistoryErrorStatus: LiveData<Error> = _orderHistoryErrorStatus

    private val _cartErrorStatus = MutableLiveData(Error())
    val cartErrorStatus: LiveData<Error> = _cartErrorStatus

    val selectedArray: HashMap<Stock, Int> = HashMap()

    private val customerRepository =
        CustomerRepository(application, Stocker.getInstance()!!.customer!!.customerId)
    private var filterOnStocks = false
    private var filterOnOrders = false

    private lateinit var job: Job

    private lateinit var selectedStockIds: Array<String>
    private lateinit var selectedStockNames: Array<String>
    private lateinit var selectedStockPrices: Array<Long>

    init {

        getAllStocks()
        getAllOrderHistory()
    }

    fun getAllStocks() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = customerRepository.getAllStocks()
                originalStocks = data.toMutableList()
                _stocksLiveData.postValue(mutableListOf<Stock>().apply {
                    addAll(data)

                })
            } catch (e: Exception) {
                _stockErrorStatus.postValue(_stockErrorStatus.value?.apply {
                    job.cancel()
                    isHandled = false
                    msg = cantRetrieveData
                })
            }
        }
    }

    fun getAllOrderHistory() {
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = customerRepository.getAllOrderHistory()
                originalOrderHistories = data.toMutableList()
                _orderHistoryLiveData.postValue(mutableListOf<OrderHistory>().apply {
                    addAll(data)
                })
            } catch (e: Exception) {
                e.printStackTrace()
                _orderHistoryErrorStatus.postValue(_orderHistoryErrorStatus.value?.apply {
                    job.cancel()
                    isHandled = false
                    msg = cantRetrieveData
                })
            }
        }
    }

    suspend fun calcCart(): StockInCart {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val selectedStocks = selectedArray.keys.toTypedArray()
            val selectedStockCount = selectedArray.values.toTypedArray()
            var total = 0L
            selectedStockIds = Array(selectedStocks.size) { "" }
            selectedStockNames = Array(selectedStocks.size) { "" }
            selectedStockPrices = Array(selectedStocks.size) { 0 }


            for (i in selectedStocks.indices) {
                val stock = selectedStocks[i]
                selectedStockIds[i] = stock.stockID
                selectedStockNames[i] = stock.stockName
                val price = calcPrice(stock.price, stock.discount, selectedStockCount[i])
                selectedStockPrices[i] = price
                total += price
            }
            StockInCart(
                selectedStockNames,
                selectedStockIds,
                selectedStockCount,
                selectedStockPrices,
                total
            )


        }

    }

    suspend fun placeOrder(total: Long): Boolean {

        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val id = IdGenerator.generateId() ?: return@withContext false

            val result = customerRepository.placeOrder(
                orderId = id,
                stocks = selectedArray,
                stockIds = selectedStockIds,
                stockNames = selectedStockNames,
                stockPrices = selectedStockPrices,
                counts = selectedArray.values.toTypedArray(),
                total = total
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

    fun sortOrderHistoryByTotalPrice(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {
            _orderHistoryLiveData.value?.let {
                _orderHistoryLiveData.postValue(
                    customerRepository.sortOrderHistoryByTotalPrice(
                        it.toMutableList(),
                        order
                    )
                )
            }
            originalOrderHistories =
                customerRepository.sortOrderHistoryByTotalPrice(originalOrderHistories, order)
                    .toMutableList()
        }

    }

    fun sortOrderHistoryByDate(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {
            _orderHistoryLiveData.value?.let {
                _orderHistoryLiveData.postValue(
                    customerRepository.sortOrderHistoryByDate(
                        it.toMutableList(),
                        order
                    )
                )
            }
            originalOrderHistories =
                customerRepository.sortOrderHistoryByDate(originalOrderHistories, order)
                    .toMutableList()
        }
    }

    fun filterOrderHistoryByStockId(filter: String) {

        job = viewModelScope.launch(Dispatchers.IO) {
            if (filter.isEmpty()) {
                _orderHistoryLiveData.postValue(originalOrderHistories)
            } else {
                _orderHistoryLiveData.value?.let {
                    _orderHistoryLiveData.postValue(
                        customerRepository.filterOrderHistoryByStockId(
                            originalOrderHistories,
                            filter
                        )
                    )

                }
            }
            filterOnOrders = true
        }
    }

    fun sortStockByPrice(order: SortUtil.SortOrder) {

        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let {
                _stocksLiveData.postValue(
                    customerRepository.sortStockByPrice(
                        it.toMutableList(),
                        order
                    )
                )

            }
            originalStocks = customerRepository.sortStockByPrice(originalStocks, order)
        }

    }

    fun sortStockByCount(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {

            _stocksLiveData.value?.let {
                _stocksLiveData.postValue(
                    customerRepository.sortStockByCount(
                        it.toMutableList(),
                        order
                    )
                )
            }
            originalStocks = customerRepository.sortStockByCount(originalStocks, order)
        }
    }

    fun sortStockByName(order: SortUtil.SortOrder) {
        job = viewModelScope.launch(Dispatchers.IO) {
            _stocksLiveData.value?.let {
                _stocksLiveData.postValue(
                    customerRepository.sortStockByName(
                        it.toMutableList(),
                        order
                    )
                )
            }
            originalStocks = customerRepository.sortStockByName(originalStocks, order)
        }
    }

    fun filterStockByName(filter: String) {
        job = viewModelScope.launch(Dispatchers.IO) {
            filterOnStocks = true

            if (filter.isEmpty()) {
                _stocksLiveData.postValue(originalStocks)
            } else {
                _stocksLiveData.value?.let {

                    _stocksLiveData.postValue(
                        customerRepository.filterStockByName(
                            originalStocks,
                            filter
                        )
                    )
                }
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

    fun clearOrderFilter(): Boolean {
        if (filterOnOrders) {
            getAllOrderHistory()
            filterOnOrders = false
            return filterOnOrders
        }
        return false
    }

    fun join(lam: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            job.join()
            if (!job.isCancelled) {
                lam()
            }

        }
    }

    private fun calcPrice(price: Int, discount: Int, count: Int): Long {
        val total = price.toLong() * count
        return (total - (total * (discount / 100f)).roundToInt())
    }

    private fun clearSelection() {
        selectedArray.clear()
        selectedStockIds = arrayOf("")

    }

    private fun updatePurchase(newOrder: OrderHistory) {

        _orderHistoryLiveData.value?.let {
            try {
                originalOrderHistories.add(0, newOrder)
                val newData =
                    _orderHistoryLiveData.value!!.toMutableList().apply { add(newOrder) }
                _orderHistoryLiveData.postValue(newData)
            } catch (e: Exception) {
                _cartErrorStatus.postValue(_cartErrorStatus.value?.apply {
                    isHandled = false
                    msg = otherError
                })
            }
        }
    }
}