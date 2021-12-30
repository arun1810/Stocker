package com.example.stocker.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

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
        val height = DisplayUtil.getDisplaySize(activity!!).x
        val toolbar = view.findViewById<MaterialToolbar>(R.id.admin_customer_toolbar)
        val addCustomerFab=view.findViewById<FloatingActionButton>(R.id.admin_add_customer_floating_btn)
        val navHost = activity!!.supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)
        val nameSort = view.findViewById<SortImageButton>(R.id.name_sort)
        nameSort.ascIcon=R.drawable.ic_sort_alphabetical_ascending
        nameSort.dscIcon=R.drawable.ic_sort_alphabetical_descending

        nameSort.setOnClickListener {
            nameSort.changeSortOrder()
            model.sortCustomerByName(nameSort.sortOrder)
            model.join { recycler.smoothScrollToPosition(0) }
            dateSortBtn.changeToDefaultSortOrder()
        }

        dateSortBtn.ascIcon=R.drawable.ic_sort_clock_ascending_outline
        dateSortBtn.dscIcon=R.drawable.ic_sort_clock_descending_outline
        dateSortBtn.setOnClickListener {
            dateSortBtn.changeSortOrder()
            model.sortCustomerByName(dateSortBtn.sortOrder)
            model.join {
                recycler.smoothScrollToPosition(0)
                nameSort.changeToDefaultSortOrder()
            }

        }



        adapter = CustomerAdapter(context!!,
            model.selectedCustomer,object:SelectionListener{
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
        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_INDEFINITE).setAction("close") {
                        status.isHandled = true
                    }.show()
                }
            }

        })


        toolbar.title="Customers"
        toolbar.inflateMenu(R.menu.customer_order_history_menu)
        val searchMenu = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        model.filterCustomerByName(it)
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
                    println("clear")
                    model.clearCustomerFilter()

                    true
                }
                R.id.edit->{
                    println("edit")
                    navController.navigate(R.id.customerDetailsGetterFragment2, bundleOf("mode" to Mode.Update))
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
            top=DisplayUtil.DpToPixel(activity!!,12),
            side = DisplayUtil.DpToPixel(activity!!,8)
        ))
        recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy>0 && addCustomerFab.isShown){
                    addCustomerFab.hide()
                }
                else if(dy<0 && (!addCustomerFab.isVisible)){
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