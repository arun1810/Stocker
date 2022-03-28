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
import com.example.stocker.pojo.Stocker
import com.example.stocker.view.adapter.CustomerMode
import com.example.stocker.view.adapter.OrderHistoryAdapter
import com.example.stocker.view.adapter.decorator.SimpleDecorator
import com.example.stocker.view.customviews.SortImageButton
import com.example.stocker.view.fragments.util.SharedPreferenceHelper
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.CustomerViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class OrderHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:OrderHistoryAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var dateSortBtn:SortImageButton
    private lateinit var priceSortBtn:SortImageButton
    private lateinit var searchMenu:SearchView
    private val model:CustomerViewModel by activityViewModels()

    private lateinit var dataStatusTextView:MaterialTextView
    private var dataChangedBy = delete

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_order__history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController  = navHost.navController

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)
        dataStatusTextView=view.findViewById(R.id.data_status_textview)


        model.orderHistoryErrorStatus.observe(viewLifecycleOwner) { status ->
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
                }
            }

        }

        toolbar = view.findViewById(R.id.order_history_toolbar)
        recyclerView = view.findViewById(R.id.recycler)
        priceSortBtn = view.findViewById(R.id.price_sort)
        dateSortBtn = view.findViewById(R.id.date_sort)

        adapter = OrderHistoryAdapter(requireContext(),navController, CustomerMode)
        val linearLayoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager =linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SimpleDecorator(
            top=requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)/2,
            side = requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)
        ))

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title=context?.getString(R.string.your_orders)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        toolbar.inflateMenu(R.menu.admin_menu)
        searchMenu = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint="Stock ID"
        searchMenu.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
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
                    Stocker.logout()
                    SharedPreferenceHelper.writeCustomerPreference(requireActivity(),null)
                    navController.navigate(R.id.action_order_History_fragment_to_loginActivity)
                    requireActivity().finish()

                    true
                }
                else -> {false}
            }
        }


        priceSortBtn.ascIcon=R.drawable.ic_price_ascending
        priceSortBtn.decIcon=R.drawable.ic_price_descending
        priceSortBtn.neutralIcon=R.drawable.ic_price_neutral

        priceSortBtn.setOnClickListener {
            priceSortBtn.changeSortOrder()
            model.sortOrderHistoryByTotalPrice(priceSortBtn.sortOrder)
            model.join {
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
                priceSortBtn.changeToDefaultSortOrder()
            }

        }
    }

    override fun onStart() {
        super.onStart()

        model.orderHistoryLiveData.observe(this) {
            if (it.isEmpty()) {
                dataStatusTextView.visibility = View.VISIBLE
                when (dataChangedBy) {
                    search -> dataStatusTextView.text = getString(R.string.couldnt_find_anything)
                    delete -> dataStatusTextView.text = getString(R.string.empty)
                }
            } else {
                dataStatusTextView.visibility = View.INVISIBLE
            }
            adapter.setNewList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchMenu.setQuery("",true)
    }
}