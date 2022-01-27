package com.example.stocker.view.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
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
import com.example.stocker.view.fragments.util.Type
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.helper.cantRetrieveData
import com.example.stocker.viewmodel.helper.deleteError
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

const val search=-1
const val delete=-2
class AdminCustomerFragment : Fragment() {

    private val model :AdminViewModel by activityViewModels()
    private lateinit var adapter: CustomerAdapter
    private lateinit var toolbar:MaterialToolbar

    private lateinit var dataStatusTextView:MaterialTextView
    private var dataChangedBy = delete



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("admin customer frag view created")
        if(model.selectedStocks.isNotEmpty()) model.clearStockSelection(Type.Nothing)

        return inflater.inflate(R.layout.fragment_admin_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recycler = view.findViewById<RecyclerView>(R.id.admin_customer_recycler)
        toolbar = view.findViewById(R.id.admin_customer_toolbar)
        val addCustomerFab=view.findViewById<FloatingActionButton>(R.id.admin_add_customer_floating_btn)
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        val navController  = navHost.navController
        val dateSortBtn = view.findViewById<SortImageButton>(R.id.date_sort)
        val nameSort = view.findViewById<SortImageButton>(R.id.name_sort)

        val errorRetryBtn: MaterialButton = view.findViewById(R.id.banner_positive_btn)
        val errorDismissBtn: MaterialButton = view.findViewById(R.id.banner_negative_btn)
        val errorTitle: MaterialTextView = view.findViewById(R.id.error_title)
        val banner = view.findViewById<ConstraintLayout>(R.id.banner)

        dataStatusTextView = view.findViewById(R.id.data_status_textview)

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
            model.sortCustomerByDOB(dateSortBtn.sortOrder)
            model.join {
                //recycler.smoothScrollBy(0,0)
                nameSort.changeToDefaultSortOrder()
            }

        }



        adapter = CustomerAdapter(requireContext(),
            model.selectedCustomer,navController,object:SelectionListener{
            override fun onOneSelect() {
                toolbar.menu.setGroupVisible(R.id.search_group,false)
                toolbar.menu.setGroupVisible(R.id.edit_group,true)
                setUpSelectionMenuSelectionOption(1)
            }
            override fun onMultipleSelect(count:Int) {
                toolbar.menu.setGroupVisible(R.id.search_group,false)
                toolbar.menu.setGroupVisible(R.id.edit_group,true)
                toolbar.menu.findItem(R.id.edit).isVisible=false
                setUpSelectionMenuSelectionOption(count)
            }
            override fun selectionDisabled() {
                toolbar.menu.setGroupVisible(R.id.search_group,true)
                toolbar.menu.setGroupVisible(R.id.edit_group,false)
                clearSelectionMenu()
            }
        })

        model.customerErrorStatus.observe(viewLifecycleOwner){status->
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
                    println("admin customer error  handled")
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
                        (DisplayUtil.dpToPixel(requireActivity(), 112).toFloat())
                    ).apply {
                        duration = 1000
                        start()
                    }
                }else{println("admin customer error  handled")}
            }

        }

        toolbar.title="Customers"
        toolbar.inflateMenu(R.menu.admin_menu)
        val searchMenu = toolbar.menu.findItem(R.id.order_search).actionView as SearchView
        searchMenu.queryHint="Customer Name"
        searchMenu.background=null
        searchMenu.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        /*model.filterCustomerByName(it)
                        if(!addCustomerFab.isShown)addCustomerFab.show()

                         */
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let{
                        //adapter.filter.filter(newText)
                        dataChangedBy = if(newText==""){
                            delete
                        } else{
                            search
                        }
                        model.filterCustomerByName(newText)
                    if(!addCustomerFab.isShown)addCustomerFab.show()
                    }
                    return false
                }

            }
        )
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){

                R.id.edit->{
                    println("edit")
                    navController.navigate(R.id.action_adminCustomerFragment_to_customerDetailsGetterFragment2, bundleOf("mode" to Mode.Update))
                    true
                }
                R.id.delete->{
                    println("delete")
                    model.removeCustomer()
                    dataChangedBy= delete
                    true
                }
                R.id.logout->{
                    SharedPreferenceHelper.writeAdminPreference(requireActivity(),false)
                    requireActivity().finish()
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
            top=requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)/2,
            side = requireActivity().resources.getDimensionPixelSize(R.dimen.gutter)
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

        model.customersLiveData.observe(this) { customers ->
            if (customers.isEmpty()) {
                dataStatusTextView.visibility = View.VISIBLE
                when (dataChangedBy) {
                    search -> dataStatusTextView.text = getString(R.string.couldnt_find_anything)
                    delete -> dataStatusTextView.text = getString(R.string.empty)
                }
            } else {
                dataStatusTextView.visibility = View.INVISIBLE
            }
            adapter.setNewList(customers)

        }

        model.customerSelectionState.observe(this) { type ->
            adapter.selectionListChanged(type)
        }

    }

    private fun setUpSelectionMenuSelectionOption(selectionCount:Int){
        toolbar.title = "$selectionCount selected"

        if(toolbar.navigationIcon==null) {
            toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_close_24)
            toolbar.setNavigationOnClickListener {
                model.clearCustomerSelection(Type.Update)
                clearSelectionMenu()
            }
        }
    }

    private fun clearSelectionMenu(){
        toolbar.title = "Customers"
        toolbar.navigationIcon = null
    }

    override fun onDestroy() {
        super.onDestroy()
        println("admin customer frag destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("admin customer frag created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("admin customer frag view destroyed")
    }
}