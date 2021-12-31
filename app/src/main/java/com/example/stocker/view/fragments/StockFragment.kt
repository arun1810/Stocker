package com.example.stocker.view.fragments

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.stocker.R
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.adapter.decorator.StockDecorator

import com.example.stocker.view.adapter.StockAdapter
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.CustomerViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class StockFragment : Fragment() {
    private lateinit var adapter:StockAdapter
    private lateinit var recycler:RecyclerView
    private val model:CustomerViewModel by activityViewModels()
    private lateinit var buyFab:FloatingActionButton
    private lateinit var toolBar:MaterialToolbar
    private lateinit var searchMenu:SearchView

    private lateinit var countSort:SortImageButton
    private lateinit var priceSort:SortImageButton
    private lateinit var nameSort:SortImageButton



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_stock_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_LONG).setAction("close") {
                        status.isHandled = true
                    }.show()

                }
            }

        })

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController  = navHost.navController


        recycler = view.findViewById(R.id.recycler)
        toolBar = view.findViewById(R.id.customer_stock_toolbar)
        buyFab = view.findViewById(R.id.buy_button)
        countSort = view.findViewById(R.id.count_sort)
        nameSort = view.findViewById(R.id.name_sort)
        priceSort = view.findViewById(R.id.price_sort)

        nameSort.ascIcon=R.drawable.ic_sort_alphabetical_ascending
        nameSort.dscIcon=R.drawable.ic_sort_alphabetical_descending

        toolBar.title="Stocks"

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

        context?.let {
            val size = DisplayUtil.getDisplaySize(activity!!)
            val spanCount:Int = if(DisplayUtil.getOrientation(activity!!)==ORIENTATION_PORTRAIT){
                adapter = StockAdapter(
                    context = it,
                    selectedArray = model.selectedArray,
                    width=size.y/2,
                    viewHeight = (size.x/3)+DisplayUtil.DpToPixel(activity!!,8),
                    stockViewHeight = (size.x/3),
                    btnLayoutHeight = DisplayUtil.DpToPixel(activity!!,24),
                    parent = recycler
                )
                recycler.addItemDecoration(StockDecorator(DisplayUtil.DpToPixel(activity!!,8)))
                2
            } else{
                adapter = StockAdapter(
                    it,
                    model.selectedArray,
                    width=size.y/3,
                    viewHeight = (size.x)+DisplayUtil.DpToPixel(activity!!,16),
                    stockViewHeight = (size.x),
                    btnLayoutHeight = DisplayUtil.DpToPixel(activity!!,24),
                    parent = recycler
                )
                recycler.addItemDecoration(StockDecorator(DisplayUtil.DpToPixel(activity!!,24)))
                3
            }

            recycler.adapter =adapter
            recycler.layoutManager = StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
        }

        recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy>0 && buyFab.isShown){
                    buyFab.hide()

                }
                else if(dy<0 && !(buyFab.isVisible)){
                    buyFab.show()

                }
            }
        })

        buyFab.setOnClickListener {
            if(model.selectedArray.isNotEmpty()){
                navController.navigate(R.id.action_stock_fragment_to_cartFragment)
            }
            else{
                Toast.makeText(context,"Cart Empty",Toast.LENGTH_SHORT).show()
            }

        }

        toolBar.inflateMenu(R.menu.customer_activity_menus)
        searchMenu = toolBar.menu.findItem(R.id.search_menu).actionView as SearchView

        searchMenu.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        model.filterStockByName(it)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //newText?.let { model.filterStockByName(it) }
                    return false
                }

            }
        )

        toolBar.setOnMenuItemClickListener {item->
            when(item.itemId){
                R.id.clear_filter_menu->{
                    if(model.clearStockFilter()) {
                            model.join {
                                recycler.smoothScrollToPosition(0)
                            }
                        }
                    true
                }
                R.id.order_history_menu->{
                    navController.navigate(R.id.action_stock_fragment_to_order_History_fragment)
                    true
                }
                R.id.logutmenu->{
                    Stocker.logout()
                    SharedPreferenceHelper.writeCustomerPreference(activity!!,null)
                    navController.navigate(R.id.action_stock_fragment_to_loginActivity)

                    //navController.popBackStack(R.id.stock_fragment,false)
                    //activity!!.supportFragmentManager.popBackStack()
                    //activity!!.onBackPressed()
                    activity!!.finish()
                    true
                }
                else -> {super.onOptionsItemSelected(item)}
            }
        }

    }

    override fun onStart() {
        super.onStart()

        model.stocksLiveData.observe(this,{
            adapter.setNewList(it)
        })


    }
}