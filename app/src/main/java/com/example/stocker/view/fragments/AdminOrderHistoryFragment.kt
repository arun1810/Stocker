package com.example.stocker.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.view.adapter.OrderHistoryAdapter
import com.example.stocker.view.adapter.SelectionListener
import com.example.stocker.view.adapter.decorator.OrderHistoryDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar


class AdminOrderHistoryFragment : Fragment() {
    val model : AdminViewModel by activityViewModels()
    lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_INDEFINITE).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })

        val recycler = view.findViewById<RecyclerView>(R.id.admin_order_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_order_toolbar)
        val linearLayoutManager = LinearLayoutManager(context)
        val height = DisplayUtil.getDisplaySize(activity!!).x
        val priceSort = view.findViewById<SortImageButton>(R.id.price_sort)
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)


        priceSort.ascIcon=R.drawable.ic_price_ascending_24
        priceSort.dscIcon=R.drawable.ic_price_descending_24
        priceSort.setOnClickListener {
            priceSort.changeSortOrder()
            model.sortOrderHistoryByTotalPrice(priceSort.sortOrder)
            model.join {
                recycler.smoothScrollToPosition(0)
                dateSortBtn.changeToDefaultSortOrder()
            }
        }

        dateSortBtn.ascIcon=R.drawable.ic_sort_clock_ascending_outline
        dateSortBtn.dscIcon=R.drawable.ic_sort_clock_descending_outline
        dateSortBtn.setOnClickListener {
            dateSortBtn.changeSortOrder()
            model.sortOrderHistoryByDate(dateSortBtn.sortOrder)
            model.join {
                recycler.smoothScrollToPosition(0)
                priceSort.changeToDefaultSortOrder()
            }

        }



        val smoothScroller = object: LinearSmoothScroller(context){
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
      /*  adapter = OrderHistoryAdapter(context!!,model.selectedOrders,object:SelectionListener{
            override fun onOneSelect() {
                toolbar.menu.setGroupVisible(R.id.search_group,false)
                toolbar.menu.setGroupVisible(R.id.edit_group,true)
            }
            override fun onMultipleSelect() {
                toolbar.menu.setGroupVisible(R.id.search_group,false)
                toolbar.menu.setGroupVisible(R.id.edit_group,true)
                toolbar.menu.findItem(R.id.edit).isVisible=false
            }
            override fun selectionDisabled() {
                toolbar.menu.setGroupVisible(R.id.search_group,true)
                toolbar.menu.setGroupVisible(R.id.edit_group,false)
            }

        })

       */

        adapter = OrderHistoryAdapter(context!!)
        recycler. adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(OrderHistoryDecorator(height/25))

        toolbar.title = "Order History"

        toolbar.inflateMenu(R.menu.customer_order_history_menu)


        val searchMenu:SearchView = toolbar.menu.findItem(R.id.order_search).actionView as androidx.appcompat.widget.SearchView
        searchMenu.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
                    SharedPreferenceHelper.writeAdminPreference(activity!!,false)
                    activity!!.finish()
                    true
                }

               /* R.id.edit->{
                    println("edit")
                    //model.updateOrder()
                    true
                }
                R.id.delete->{
                    println("delete")
                    model.removeOrderHistory()
                    true
                }

                */
                else -> {false}
            }
        }



    }

    override fun onStart() {
        super.onStart()

        model.orderHistoriesLiveData.observe(this,{orderHistory->
            adapter.setNewList(orderHistory)

        })

       /* model.orderSelectionState.observe(this,{
            adapter.selectionListChanged()

        })
       */
    }

}