package com.example.stocker.view.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.stocker.R
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.adapter.StockAdapter
import com.example.stocker.view.adapter.decorator.StockDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.CustomerViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.example.stocker.viewmodel.helper.deleteError
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView


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
    private lateinit var layoutManager: StaggeredGridLayoutManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_stock_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController  = navHost.navController

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)





        model.stockErrorStatus.observe(this,{status->
            status?.let {
                errorDismissBtn.setOnClickListener {
                    val x = banner.translationY
                    status.isHandled = true
                    ObjectAnimator.ofFloat(banner, "translationY", -x).apply {
                        startDelay = 100
                        duration = 700
                        start()
                    }
                }

                errorRetryBtn.setOnClickListener {
                    val x = banner.translationY
                    status.isHandled = true
                    ObjectAnimator.ofFloat(banner, "translationY", -x).apply {
                        startDelay = 100
                        addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {
                                //do nothing
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                when (status.msg) {


                                    cantRetrieveData -> {
                                        model.getAllStocks()
                                    }

                                }
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                                //do nothing
                            }

                            override fun onAnimationRepeat(animation: Animator?) {
                                //do Nothing
                            }

                        })
                        duration = 700
                        start()
                    }


                }

                if (!status.isHandled) {

                    errorTitle.text = when (status.msg) {

                        deleteError -> {
                            "can't delete customer right now. try again"
                        }
                        cantRetrieveData -> {
                            errorDismissBtn.visibility=View.GONE
                            "can't get customers right now. try again"
                        }
                        else -> {
                            ""
                        }

                    }
                    ObjectAnimator.ofFloat(
                        banner,
                        "translationY",
                        (DisplayUtil.dpToPixel(activity!!, 112).toFloat())
                    ).apply {
                        duration = 1000
                        start()
                    }
                }
            }

        })




        recycler = view.findViewById(R.id.recycler)
        toolBar = view.findViewById(R.id.customer_stock_toolbar)
        buyFab = view.findViewById(R.id.buy_button)
        countSort = view.findViewById(R.id.count_sort)
        nameSort = view.findViewById(R.id.name_sort)
        priceSort = view.findViewById(R.id.price_sort)

        nameSort.ascIcon=R.drawable.ic_sort_alphabetical_ascending
        nameSort.decIcon=R.drawable.ic_sort_alphabetical_descending
        nameSort.neutralIcon=R.drawable.ic_sort_alphabet_neutral

        toolBar.title="Stocks"

        nameSort.setOnClickListener {
            nameSort.changeSortOrder()
            model.sortStockByName(nameSort.sortOrder)
            model.join {
                countSort.changeToDefaultSortOrder()
                priceSort.changeToDefaultSortOrder()
            }

        }


        countSort.ascIcon=R.drawable.ic_count_ascending
        countSort.decIcon=R.drawable.ic_count_descending
        countSort.neutralIcon=R.drawable.ic_count_neutral

        countSort.setOnClickListener {
            countSort.changeSortOrder()
            model.sortStockByCount(countSort.sortOrder)
            model.join {
                priceSort.changeToDefaultSortOrder()
                nameSort.changeToDefaultSortOrder()
            }

        }

        priceSort.ascIcon=R.drawable.ic_price_ascending
        priceSort.decIcon=R.drawable.ic_price_descending
        priceSort.neutralIcon=R.drawable.ic_price_neutral

        priceSort.setOnClickListener {
            priceSort.changeSortOrder()
            model.sortStockByPrice(priceSort.sortOrder)
            model.join {
                nameSort.changeToDefaultSortOrder()
                countSort.changeToDefaultSortOrder()
                 }

        }

        context?.let {
            val bodyParams = DisplayUtil.getBodyParams(activity!!)
            val spanCount:Int = if(DisplayUtil.getOrientation(activity!!)==ORIENTATION_PORTRAIT){
                adapter = StockAdapter(
                    context = it,
                    selectedArray = model.selectedArray,
                    width =DisplayUtil.dpToPixel(activity!!,bodyParams.columnSize*2),
                    navController
                )
                bodyParams.numberOfColumns/2 //span count
            } else{
                adapter = StockAdapter(
                    it,
                    model.selectedArray,
                    width =DisplayUtil.dpToPixel(activity!!,bodyParams.columnSize*2),
                    navController
                )

                bodyParams.numberOfColumns/2 //span count
            }
            recycler.addItemDecoration(StockDecorator(activity!!.resources.getDimensionPixelSize(R.dimen.gutter)))
            recycler.adapter =adapter
            layoutManager=StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
            layoutManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            recycler.layoutManager = layoutManager
        }

        recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > DisplayUtil.dpToPixel(activity!!, 8) && buyFab.isShown) {
                    buyFab.hide()
                } else if (dy < -(DisplayUtil.dpToPixel(activity!!, 5)) && !buyFab.isShown) {

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
        searchMenu.queryHint="Stock Name"
        searchMenu.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        model.filterStockByName(it)
                        if(!buyFab.isShown) buyFab.show()
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