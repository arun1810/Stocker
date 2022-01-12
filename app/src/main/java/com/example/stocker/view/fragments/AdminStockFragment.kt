package com.example.stocker.view.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.stocker.R
import com.example.stocker.view.adapter.AdminStockAdapter
import com.example.stocker.view.adapter.SelectionListener
import com.example.stocker.view.adapter.decorator.StockDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.Mode
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.example.stocker.viewmodel.helper.deleteError
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView


class AdminStockFragment : Fragment() {
    val model: AdminViewModel by activityViewModels()
    private lateinit var adapter: AdminStockAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return inflater.inflate(R.layout.fragment_admin_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.admin_stock_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_stock_toolbar)
        val addStockFab = view.findViewById<FloatingActionButton>(R.id.admin_add_stock_fab)
        val navHost =
            activity!!.supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController = navHost.navController
        val countSort = view.findViewById<SortImageButton>(R.id.count_sort)
        val nameSort = view.findViewById<SortImageButton>(R.id.name_sort)
        val priceSort = view.findViewById<SortImageButton>(R.id.price_sort)

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)

        model.stockErrorStatus.observe(this, { status ->
            status?.let {

                errorDismissBtn.setOnClickListener {
                    val y = banner.translationY
                    status.isHandled = true
                    ObjectAnimator.ofFloat(banner, "translationY", -y).apply {
                        startDelay = 100
                        duration = 700
                        start()
                    }
                }

                errorRetryBtn.setOnClickListener {
                    val y = banner.translationY
                    status.isHandled = true
                    ObjectAnimator.ofFloat(banner, "translationY",-y).apply {
                        startDelay = 100
                        addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {
                                //do nothing
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                when (status.msg) {

                                    deleteError -> {
                                        model.removeStock()
                                    }
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
                            "can't delete stocks right now. try again"
                        }
                        cantRetrieveData -> {
                            errorDismissBtn.visibility=View.GONE
                            "can't get stocks right now. try again"
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


        nameSort.ascIcon = R.drawable.ic_sort_alphabetical_ascending
        nameSort.decIcon = R.drawable.ic_sort_alphabetical_descending
        nameSort.neutralIcon = R.drawable.ic_sort_alphabet_neutral

        nameSort.setOnClickListener {
            nameSort.changeSortOrder()
            model.sortStockByName(nameSort.sortOrder)
            model.join {
                //  layoutManager.scrollToPositionWithOffset(0,0)
            }
            countSort.changeToDefaultSortOrder()
            priceSort.changeToDefaultSortOrder()
        }


        countSort.ascIcon = R.drawable.ic_count_ascending
        countSort.decIcon = R.drawable.ic_count_descending
        countSort.neutralIcon = R.drawable.ic_count_neutral

        countSort.setOnClickListener {
            countSort.changeSortOrder()
            model.sortStockByCount(countSort.sortOrder)
            model.join {
                //  layoutManager.scrollToPositionWithOffset(0,0)
            }
            priceSort.changeToDefaultSortOrder()
            nameSort.changeToDefaultSortOrder()
        }

        priceSort.ascIcon = R.drawable.ic_price_ascending
        priceSort.decIcon = R.drawable.ic_price_descending
        priceSort.neutralIcon = R.drawable.ic_price_neutral

        priceSort.setOnClickListener {
            priceSort.changeSortOrder()
            model.sortStockByPrice(priceSort.sortOrder)
            nameSort.changeToDefaultSortOrder()
            countSort.changeToDefaultSortOrder()
        }

        toolbar.title = "Stocks"
        toolbar.inflateMenu(R.menu.customer_order_history_menu)

        val searchMenu: SearchView =
            toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint = "Stock Name"
        searchMenu.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //model.filterStockByName(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    adapter.filter.filter(newText)

                    if(!addStockFab.isShown) addStockFab.show()
                }

                return false
            }

        })
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.order_clear -> {

                    if (model.clearStockFilter()) {
                        model.join {
                            recycler.scrollToPosition(0)
                        }
                    }
                    true
                }

                R.id.logout -> {
                    SharedPreferenceHelper.writeAdminPreference(activity!!, false)
                    navController.navigate(R.id.action_adminStockFragment_to_loginActivity)
                    activity!!.finish()
                    true
                }

                R.id.edit -> {
                    println("edit")
                    navController.navigate(
                        R.id.action_adminStockFragment_to_stockDetailsGetterFragment,
                        bundleOf("mode" to Mode.Update)
                    )
                    true
                }
                R.id.delete -> {
                    println("delete")
                    model.removeStock()
                    true
                }
                else -> {
                    false
                }
            }
        }

        val selectionListener = object : SelectionListener {
            override fun onOneSelect() {
                toolbar.menu.setGroupVisible(R.id.search_group, false)
                toolbar.menu.setGroupVisible(R.id.edit_group, true)
            }

            override fun onMultipleSelect() {
                toolbar.menu.setGroupVisible(R.id.search_group, false)
                toolbar.menu.setGroupVisible(R.id.edit_group, true)
                toolbar.menu.findItem(R.id.edit).isVisible = false
            }

            override fun selectionDisabled() {
                toolbar.menu.setGroupVisible(R.id.search_group, true)
                toolbar.menu.setGroupVisible(R.id.edit_group, false)
            }
        }

        context?.let {
            val bodyParams = DisplayUtil.getBodyParams(activity!!)
            val spanCount: Int =
                if (DisplayUtil.getOrientation(activity!!) == Configuration.ORIENTATION_PORTRAIT) {
                    adapter = AdminStockAdapter(
                        it,
                        model.selectedStocks,
                        width=DisplayUtil.dpToPixel(activity!!,bodyParams.columnSize*2),
                        navController,

                        selectionListener = selectionListener
                    )
                    recycler.addItemDecoration(StockDecorator(activity!!.resources.getDimensionPixelSize(R.dimen.gutter)))

                    bodyParams.numberOfColumns/2 // span count
                } else {
                    adapter = AdminStockAdapter(
                        it,
                        model.selectedStocks,
                        width = DisplayUtil.dpToPixel(activity!!,bodyParams.columnSize*2),
                        navController,
                        selectionListener = selectionListener
                    )
                   recycler.addItemDecoration(StockDecorator(activity!!.resources.getDimensionPixelSize(R.dimen.gutter)))
                    bodyParams.numberOfColumns/2 //span count
                }

            recycler.adapter = adapter
            layoutManager = StaggeredGridLayoutManager(
                spanCount,
                StaggeredGridLayoutManager.VERTICAL
            )
            layoutManager.gapStrategy =
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

            recycler.layoutManager = layoutManager

        }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > DisplayUtil.dpToPixel(activity!!, 8) && addStockFab.isShown) {
                    addStockFab.hide()
                }
                else if (dy < -(DisplayUtil.dpToPixel(activity!!, 5)) && !addStockFab.isShown) {

                    addStockFab.show()
                }
            }
        })
        addStockFab.setOnClickListener {
            navController.navigate(
                R.id.action_adminStockFragment_to_stockDetailsGetterFragment,
                bundleOf("mode" to Mode.Create)
            )
        }

    }

    override fun onStart() {
        super.onStart()
        model.stockLiveData.observe(this, { stocks ->
            adapter.setNewList(stocks)

        })

        model.stockSelectionState.observe(this, { type ->
            adapter.selectionListChanged(type)
        })
    }

}