package com.example.stocker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.pojo.OrderHistory
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.adapter.OrderHistoryAdapter
import com.example.stocker.view.adapter.decorator.OrderHistoryDecorator
import com.example.stocker.view.adapter.decorator.SimpleDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.CustomerViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class OrderHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:OrderHistoryAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var dateSortBtn:SortImageButton
    private lateinit var priceSortBtn:SortImageButton
    private lateinit var searchMenu:SearchView
    private val model:CustomerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_order__history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController  = navHost.navController

        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_LONG).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })

        val height = DisplayUtil.getDisplaySize(activity!!).x

        toolbar = view.findViewById(R.id.order_history_toolbar)
        recyclerView = view.findViewById(R.id.recycler)
        priceSortBtn = view.findViewById(R.id.price_sort)
        dateSortBtn = view.findViewById(R.id.date_sort)

        adapter = OrderHistoryAdapter(context!!)
        val linearLayoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager =linearLayoutManager

        val smoothScroller = object:LinearSmoothScroller(context){
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SimpleDecorator(
            top=DisplayUtil.DpToPixel(activity!!,12),
            side = DisplayUtil.DpToPixel(activity!!,8)
        ))

        toolbar.title = "Order History"
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        toolbar.inflateMenu(R.menu.customer_order_history_menu)
        searchMenu = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    model.filterOrderHistoryByStockId(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        toolbar.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.order_clear->{
                    if(model.clearOrderFilter()){
                        model.join {
                            smoothScroller.targetPosition=0
                        linearLayoutManager.startSmoothScroll(smoothScroller)
                        }
                    }
                    true
                }
                R.id.logout->{
                    Stocker.logout()
                    SharedPreferenceHelper.writeCustomerPreference(activity!!,null)
                    navController.navigate(R.id.action_order_History_fragment_to_loginActivity)
                    activity!!.finish()

                    true
                }
                else -> {false}
            }
        }

        priceSortBtn.dscIcon=R.drawable.ic_price_descending_24
        priceSortBtn.ascIcon=R.drawable.ic_price_ascending_24

        priceSortBtn.setOnClickListener {
            priceSortBtn.changeSortOrder()
            model.sortOrderHistoryByTotalPrice(priceSortBtn.sortOrder)
            model.join {
                smoothScroller.targetPosition=0
                linearLayoutManager.startSmoothScroll(smoothScroller)
            }
            dateSortBtn.changeToDefaultSortOrder()

        }

        dateSortBtn.ascIcon=R.drawable.ic_sort_clock_ascending_outline
        dateSortBtn.dscIcon=R.drawable.ic_sort_clock_descending_outline
        dateSortBtn.setOnClickListener {
            dateSortBtn.changeSortOrder()
            model.sortOrderHistoryByDate(dateSortBtn.sortOrder)
            model.join {
                smoothScroller.targetPosition=0
                linearLayoutManager.startSmoothScroll(smoothScroller)
            }
            priceSortBtn.changeToDefaultSortOrder()
        }
    }

    override fun onStart() {
        super.onStart()
        val model:CustomerViewModel by activityViewModels()

        model.orderHistoryLiveData.observe(this,{
            adapter.setNewList(it)
        })
    }
}