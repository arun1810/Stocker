package com.example.stocker.view.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.stocker.R
import com.example.stocker.view.adapter.AdminStockAdapter
import com.example.stocker.view.adapter.SelectionListener
import com.example.stocker.view.adapter.StockAdapter
import com.example.stocker.view.adapter.decorator.StockDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.Mode
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class AdminStockFragment : Fragment() {
    val model:AdminViewModel by activityViewModels()
    private lateinit var adapter: AdminStockAdapter

override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return inflater.inflate(R.layout.fragment_admin_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.admin_stock_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_stock_toolbar)
        val addStockFab = view.findViewById<FloatingActionButton>(R.id.admin_add_stock_fab)
        val height = DisplayUtil.getDisplaySize(activity!!).x
        val width = DisplayUtil.getDisplaySize(activity!!).y
        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController
        val countSort = view.findViewById<SortImageButton>(R.id.count_sort)
        val nameSort = view.findViewById<SortImageButton>(R.id.name_sort)
        val priceSort = view.findViewById<SortImageButton>(R.id.price_sort)


        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_INDEFINITE).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })


        nameSort.ascIcon=R.drawable.ic_sort_alphabetical_ascending
        nameSort.dscIcon=R.drawable.ic_sort_alphabetical_descending

        nameSort.setOnClickListener {
            nameSort.changeSortOrder()
            model.sortStockByName(nameSort.sortOrder)
            model.join { recycler.smoothScrollToPosition(0) }
            countSort.changeToDefaultSortOrder()
            priceSort.changeToDefaultSortOrder()
        }

        countSort.dscIcon=R.drawable.ic_count_descending
        countSort.ascIcon=R.drawable.ic_count_ascending
        countSort.setOnClickListener {
            countSort.changeSortOrder()
            model.sortStockByCount(countSort.sortOrder)
            model.join { recycler.smoothScrollToPosition(0) }
            priceSort.changeToDefaultSortOrder()
            nameSort.changeToDefaultSortOrder()
        }

        priceSort.ascIcon=R.drawable.ic_price_ascending_24
        priceSort.dscIcon=R.drawable.ic_price_descending_24
        priceSort.setOnClickListener {
            priceSort.changeSortOrder()
            model.sortStockByPrice(priceSort.sortOrder)
            model.join {
                recycler.smoothScrollToPosition(0) }
            nameSort.changeToDefaultSortOrder()
            countSort.changeToDefaultSortOrder()
        }

        toolbar.title="Stocks"
        toolbar.inflateMenu(R.menu.customer_order_history_menu)

        val searchMenu: SearchView = toolbar.menu.findItem(R.id.order_search).actionView as androidx.appcompat.widget.SearchView
        searchMenu.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    model.filterStockByName(query)
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

                    if(model.clearStockFilter()){
                        model.join {
                             //smoothScroller.targetPosition=0
                            //linearLayoutManager.startSmoothScroll(smoothScroller)
                        }
                    }
                    true
                }

                R.id.logout->{
                    SharedPreferenceHelper.writeAdminPreference(activity!!,false)
                    navController.navigate(R.id.action_adminStockFragment_to_loginActivity)
                    activity!!.finish()
                    true
                }

                R.id.edit->{
                    println("edit")
                    navController.navigate(R.id.stockDetailsGetterFragment, bundleOf("mode" to Mode.Update))
                    true
                }
                R.id.delete->{
                    println("delete")
                    model.removeStock()
                    true
                }
                else -> {false}
            }
        }

        val selectionListener = object:SelectionListener{
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
        }

        context?.let {
            val size = DisplayUtil.getDisplaySize(activity!!)
            val spanCount:Int = if(DisplayUtil.getOrientation(activity!!)== Configuration.ORIENTATION_PORTRAIT){
                adapter = AdminStockAdapter(it,
                    model.selectedStocks,
                    height = size.x/3,
                    width = size.y/2,
                    selectionListener = selectionListener
                )
                recycler.addItemDecoration(StockDecorator(DisplayUtil.DpToPixel(activity!!,8)))

                2
            } else{
                adapter = AdminStockAdapter(
                    it,
                    model.selectedStocks,
                    height = size.x,
                    width = size.y/3,
                    selectionListener = selectionListener)
                recycler.addItemDecoration(StockDecorator(DisplayUtil.DpToPixel(activity!!,24)))
                3
            }

            recycler.adapter =adapter
            recycler.layoutManager = StaggeredGridLayoutManager(spanCount,
                StaggeredGridLayoutManager.VERTICAL)

        }

        recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy>0 && addStockFab.isShown){
                    addStockFab.hide()
                }
                else if(dy<0 && (!addStockFab.isVisible)){

                    addStockFab.show()
                }
            }
        })
        addStockFab.setOnClickListener {
            navController.navigate(R.id.action_adminStockFragment_to_stockDetailsGetterFragment, bundleOf("mode" to Mode.Create))
        }

    }

    override fun onStart() {
        super.onStart()
        model.stockLiveData.observe(this,{stocks->
            adapter.setNewList(stocks)

        })

        model.stockSelectionState.observe(this,{type->
            adapter.selectionListChanged(type)
        })
    }
}