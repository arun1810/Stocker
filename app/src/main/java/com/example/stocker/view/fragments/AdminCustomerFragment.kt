package com.example.stocker.view.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.view.adapter.CustomerAdapter
import com.example.stocker.view.adapter.SelectionListener
import com.example.stocker.view.adapter.decorator.SimpleDecorator
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView

class AdminCustomerFragment : Fragment() {
    val model :AdminViewModel by activityViewModels()
    lateinit var adapter: CustomerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recycler = view.findViewById<RecyclerView>(R.id.admin_customer_recycler)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_customer_toolbar)
        val addCustomerFab=view.findViewById<FloatingActionButton>(R.id.admin_add_customer_floating_btn)
        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)
        val nameSort = view.findViewById<SortImageButton>(R.id.name_sort)

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)

        nameSort.ascIcon=R.drawable.ic_sort_alphabetical_ascending
        nameSort.decIcon=R.drawable.ic_sort_alphabetical_descending
        nameSort.neutralIcon=R.drawable.ic_sort_alphabet_neutral

        nameSort.setOnClickListener {
            nameSort.changeSortOrder()
            model.sortCustomerByName(nameSort.sortOrder)
            model.join {

                dateSortBtn.changeToDefaultSortOrder()
            }

        }

        dateSortBtn.ascIcon=R.drawable.ic_sort_clock_ascending_outline
        dateSortBtn.decIcon=R.drawable.ic_sort_clock_descending_outline
        dateSortBtn.neutralIcon=R.drawable.ic_sort_clock_neutral_outline
        dateSortBtn.setOnClickListener {
            dateSortBtn.changeSortOrder()
            model.sortCustomerByName(dateSortBtn.sortOrder)
            model.join {
                //recycler.smoothScrollBy(0,0)
                nameSort.changeToDefaultSortOrder()
            }

        }



        adapter = CustomerAdapter(context!!,
            model.selectedCustomer,navController,object:SelectionListener{
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

        model.customerErrorStatus.observe(this){status->
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

                                    deleteError -> {
                                        model.removeCustomer()
                                    }
                                    cantRetrieveData -> {
                                        model.getAllCustomers()
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

        }

        toolbar.title="Customers"
        toolbar.inflateMenu(R.menu.customer_order_history_menu)
        val searchMenu = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint="Customer Name"
        searchMenu.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        model.filterCustomerByName(it)
                        if(!addCustomerFab.isShown)addCustomerFab.show()
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //newText?.let { model.filterStockByName(it) }
                    return false
                }

            }
        )
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.order_clear->{
                    if(model.clearCustomerFilter()){
                        model.join {
                            recycler.scrollToPosition(0)
                        }
                    }

                    true
                }
                R.id.edit->{
                    println("edit")
                    navController.navigate(R.id.action_adminCustomerFragment_to_customerDetailsGetterFragment2, bundleOf("mode" to Mode.Update))
                    true
                }
                R.id.delete->{
                    println("delete")
                    model.removeCustomer()
                    true
                }
                R.id.logout->{
                    SharedPreferenceHelper.writeAdminPreference(activity!!,false)
                    activity!!.finish()
                    navController.navigate(R.id.action_adminCustomerFragment_to_loginActivity)
                    true
                }

                else -> {
                    false
                }
            }
        }
        recycler.adapter = adapter
        recycler.layoutManager  = LinearLayoutManager(context)
        recycler.addItemDecoration(SimpleDecorator(
            top=activity!!.resources.getDimensionPixelSize(R.dimen.gutter)/2,
            side = activity!!.resources.getDimensionPixelSize(R.dimen.gutter)
        ))
        recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > DisplayUtil.dpToPixel(activity!!, 8) && addCustomerFab.isShown) {
                    addCustomerFab.hide()
                } else if (dy < -(DisplayUtil.dpToPixel(activity!!, 5)) && !addCustomerFab.isShown) {

                    addCustomerFab.show()
                }
            }
        })

        addCustomerFab.setOnClickListener {
            navController.navigate(R.id.action_adminCustomerFragment_to_customerDetailsGetterFragment2, bundleOf("mode" to Mode.Create))
        }

    }

    override fun onStart() {
        super.onStart()

        model.customersLiveData.observe(this,{customers->
            adapter.setNewList(customers)

        })

        model.customerSelectionState.observe(this,{type->
            adapter.selectionListChanged(type)
        })



    }
}