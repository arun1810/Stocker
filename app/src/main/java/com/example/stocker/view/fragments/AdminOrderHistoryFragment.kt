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
import com.example.stocker.view.fragments.util.Type
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView


class AdminOrderHistoryFragment : Fragment() {
    val model : AdminViewModel by activityViewModels()
    private lateinit var adapter: OrderHistoryAdapter

    private lateinit var dataStatusTextView:MaterialTextView
    private var dataChangedBy = delete


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("Admin Order History view created")
        if(model.selectedCustomer.isNotEmpty()) model.clearCustomerSelection(Type.Nothing)
        if(model.selectedStocks.isNotEmpty()) model.clearStockSelection(Type.Nothing)
        return inflater.inflate(R.layout.fragment_admin_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.admin_order_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_order_toolbar)
        val priceSort = view.findViewById<SortImageButton>(R.id.price_sort)
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)
        dataStatusTextView = view.findViewById(R.id.data_status_textview)

        model.orderErrorStatus.observe(viewLifecycleOwner) { status ->
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
                    println("admin order history error not handled")

                    errorTitle.text = when (status.msg) {


                        cantRetrieveData -> {
                            errorDismissBtn.visibility = View.GONE
                            "can't get order history right now. try again"
                        }
                        else -> {
                            ""
                        }

                    }
                    ObjectAnimator.ofFloat(
                        banner,
                        "translationY",
                        (DisplayUtil.dpToPixel(requireActivity(), 112).toFloat())
                    ).apply {
                        duration = 1000
                        start()
                    }
                } else {
                    println("admin order history error  handled")
                }
            }

        }




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
        adapter = OrderHistoryAdapter(requireContext(),navController, AdminMode)
        recycler. adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(SimpleDecorator(
            top=requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)/2,
            side = requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)
        ))

        toolbar.title = "Order History"

        toolbar.inflateMenu(R.menu.admin_menu)


        val searchMenu:SearchView = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint="Stock ID"
        searchMenu.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    //model.filterOrderHistoryByStockId(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    //adapter.filter.filter(newText)
                    dataChangedBy = if(newText==""){
                        delete
                    } else{
                        search
                    }
                    model.filterOrderHistoryByStockId(newText)
                }
                return false
            }

        })
        toolbar.setOnMenuItemClickListener { item->
            when(item.itemId){

                R.id.logout->{
                    SharedPreferenceHelper.writeAdminPreference(requireActivity(),false)
                    navController.navigate(R.id.action_adminOrderHistoryFragment_to_loginActivity)
                    requireActivity().finish()

                    true
                }
                else -> {false}
            }
        }



    }

    override fun onStart() {
        super.onStart()

        model.orderHistoriesLiveData.observe(this) { orderHistory ->

            if (orderHistory.isEmpty()) {
                dataStatusTextView.visibility = View.VISIBLE
                when (dataChangedBy) {
                    delete -> dataStatusTextView.text = getString(R.string.empty)
                    search -> dataStatusTextView.text = getString(R.string.couldnt_find_anything)
                }
            } else {
                dataStatusTextView.visibility = View.INVISIBLE
            }

            adapter.setNewList(orderHistory)


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("admin OrderHistory frag destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("admin OrderHistory frag created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("admin OrderHistory frag view destroyed")
    }

}