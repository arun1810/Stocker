package com.example.stocker.view.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.view.adapter.AdminMode
import com.example.stocker.view.adapter.OrderHistoryAdapter
import com.example.stocker.view.adapter.decorator.SimpleDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.example.stocker.viewmodel.helper.deleteError
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView


class AdminOrderHistoryFragment : Fragment() {
    val model : AdminViewModel by activityViewModels()
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.admin_order_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_order_toolbar)
        val priceSort = view.findViewById<SortImageButton>(R.id.price_sort)
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)
        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)

        model.orderErrorStatus.observe(this,{status->
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
                                        model.getAllOrderHistory()
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


                        cantRetrieveData -> {
                            errorDismissBtn.visibility=View.GONE
                            "can't get order history right now. try again"
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




        priceSort.ascIcon=R.drawable.ic_price_ascending
        priceSort.decIcon=R.drawable.ic_price_descending
        priceSort.neutralIcon=R.drawable.ic_price_neutral
        priceSort.setOnClickListener {
            priceSort.changeSortOrder()
            model.sortOrderHistoryByTotalPrice(priceSort.sortOrder)
            model.join {
                //recycler.smoothScrollToPosition(0)
                dateSortBtn.changeToDefaultSortOrder()
            }
        }

        dateSortBtn.ascIcon=R.drawable.ic_sort_clock_ascending_outline
        dateSortBtn.decIcon=R.drawable.ic_sort_clock_descending_outline
        dateSortBtn.neutralIcon=R.drawable.ic_sort_clock_neutral_outline
        dateSortBtn.setOnClickListener {
            dateSortBtn.changeSortOrder()
            model.sortOrderHistoryByDate(dateSortBtn.sortOrder)
            model.join {
                //recycler.smoothScrollToPosition(0)
                priceSort.changeToDefaultSortOrder()
            }

        }
        adapter = OrderHistoryAdapter(context!!,navController, AdminMode)
        recycler. adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(SimpleDecorator(
            top=activity!!.resources.getDimensionPixelSize(R.dimen.gutter)/2,
            side = activity!!.resources.getDimensionPixelSize(R.dimen.gutter)
        ))

        toolbar.title = "Order History"

        toolbar.inflateMenu(R.menu.customer_order_history_menu)


        val searchMenu:SearchView = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint="Stock ID"
        searchMenu.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
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
                            recycler.smoothScrollToPosition(0)
                        }
                    }
                    true
                }
                R.id.logout->{
                    SharedPreferenceHelper.writeAdminPreference(activity!!,false)
                    navController.navigate(R.id.action_adminOrderHistoryFragment_to_loginActivity)
                    activity!!.finish()

                    true
                }
                else -> {false}
            }
        }



    }

    override fun onStart() {
        super.onStart()

        model.orderHistoriesLiveData.observe(this,{orderHistory->
            adapter.setNewList(orderHistory)

        })
    }

}