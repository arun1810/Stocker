package com.example.stocker.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.view.fragments.util.Mode
import com.example.stocker.viewmodel.AdminViewModel
import com.example.stocker.viewmodel.helper.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CustomerDetailsGetterFragment : DialogFragment() {

   private lateinit var customerNameEtx:TextInputEditText
   private lateinit var customerNameLayout:TextInputLayout

   private lateinit var customerIdEtx:TextInputEditText
    private lateinit var customerIdLayout:TextInputLayout

    private lateinit var customerPasswordEtx:TextInputEditText
    private lateinit var customerPasswordLayout:TextInputLayout

    private lateinit var customerMobileNumberEtx:TextInputEditText
    private lateinit var customerMobileNumberLayout:TextInputLayout

    private lateinit var customerGenderToggleGrp:MaterialButtonToggleGroup

    private lateinit var dobBtn:MaterialButton

    private lateinit var dobErrorLabel:MaterialTextView

    private var dob = LocalDate.now()

    private var gender:Char='M'

    private val model:AdminViewModel by activityViewModels()

    private lateinit var toolbar:MaterialToolbar

    private var mode = Mode.Create

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppTheme_FullScreenDialog)
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        mode = arguments?.get("mode") as Mode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_customer_details_getter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(dialog!=null){
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog!!.window!!.setLayout(width, height)
        }

        //dialog!!.setCancelable(false)
        //val size = DisplayUtil.getDisplaySize(activity!!)
        //val height = size.x
        //val width = size.y
        //dialog!!.window!!.setLayout(width, (height/1.5).toInt())



        customerIdEtx = view.findViewById(R.id.admin_enter_customer_id)
        customerIdLayout = view.findViewById(R.id.admin_enter_customer_id_layout)
        customerPasswordEtx = view.findViewById(R.id.admin_enter_customer_password)
        customerPasswordLayout = view.findViewById(R.id.admin_enter_customer_password_layout)
        customerNameEtx = view.findViewById(R.id.admin_enter_customer_name)
        customerNameLayout = view.findViewById(R.id.admin_enter_customer_name_layout)
        customerMobileNumberEtx = view.findViewById(R.id.admin_enter_customer_mobile_number)
        customerMobileNumberLayout = view.findViewById(R.id.admin_enter_customer_mobile_number_layout)
        dobErrorLabel = view.findViewById(R.id.admin_customer_dob_btn_label)

        dobBtn = view.findViewById(R.id.admin_customer_dob_btn)
        toolbar = view.findViewById(R.id.admin_enter_customer_toolbar)
        customerGenderToggleGrp = view.findViewById(R.id.admin_customer_gender_toggle_grp)

        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        toolbar.setNavigationOnClickListener {

            dialog!!.cancel()
        }
        toolbar.inflateMenu(R.menu.dialog_menu)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add->{
                    add()
                    true
                }
                else->{
                    false
                }
            }
        }

        model.customerDetailsGetterError.observe(this) { status ->
            status?.let {
                if (!status.isHandled) {
                    val msg = when (status.msg) {
                        uniqueIdError -> {
                            "Given ID is not unique. Try another ID"
                        }
                        otherError -> {
                            "Something went wrong. Try again"
                        }
                        else -> {
                            ""
                        }
                    }
                    val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG).apply {
                        //this.view.setPadding(0,0,0,32)
                        show()
                    }

                    status.isHandled = true
                }
            }

        }


        customerGenderToggleGrp.check(R.id.admin_customer_male)

        customerGenderToggleGrp.addOnButtonCheckedListener { _, checkedId, _ ->


            when(checkedId){
                R.id.admin_customer_male->{
                    gender='M'
                }

                R.id.admin_customer_female->{
                    gender='F'
                }
            }
        }

        customerMobileNumberEtx.addTextChangedListener(object:TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {


                try {
                    if(customerMobileNumberLayout.error!=null) customerMobileNumberLayout.error=null
                    if(s.toString().toLong()<0) customerMobileNumberLayout.error="invalid number"

                } catch (e: NumberFormatException) {
                    if (s!!.isNotEmpty()) customerMobileNumberLayout.error = "invalid number"
                }


            }
        })



      customerNameEtx.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if(customerNameLayout.error!=null)customerNameLayout.error=null
            }

        })

      customerIdEtx.addTextChangedListener(object:TextWatcher{
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

          }

          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

          }

          override fun afterTextChanged(s: Editable?) {
              if(customerIdLayout.error!=null)customerIdLayout.error=null
              if(s.toString().isNotEmpty() && s.toString().contains(' '))customerIdLayout.error="invalid ID"
          }

      })
      customerPasswordEtx.addTextChangedListener(object:TextWatcher{
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

          }

          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

          }

          override fun afterTextChanged(s: Editable?) {
              if(customerPasswordLayout.error!=null)customerPasswordLayout.error=null
          }

      })



        dobBtn.setOnClickListener {

            val constraints = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())

            val picker = MaterialDatePicker.Builder.datePicker().also {
                //it.setTheme(R.style.MaterialCalendarTheme)
                it.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                it.setTitleText("select date")
                it.setCalendarConstraints(constraints.build())
            }

            val builder = picker.build().apply {

            }
            builder.addOnPositiveButtonClickListener {

                dob = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                dobBtn.setStrokeColorResource(R.color.success)
               dobBtn.text=dob.toString()
                dobErrorLabel.text=""

            }
            builder.show(parentFragmentManager,"date picker")
        }

    }

    private fun add(){
        var canAdd=true
        if(dobBtn.text==context?.getString(R.string.select_dob)){
            dobBtn.setStrokeColorResource(R.color.error)
            dobErrorLabel.text = "Please a DOB"
            canAdd=false
        }
        customerNameEtx.text?.isEmpty().apply {
            if(this!!) {
                customerNameLayout.error = "Name cannot be empty"
                canAdd = false
            }
        }
        customerPasswordEtx.text?.isEmpty().apply{
            if(this!!) {
                customerPasswordLayout.error = "Password cannot be empty"
                canAdd = false
            }
        }
        customerIdEtx.text?.isEmpty().apply{
            if(this!!) {
                customerIdLayout.error = "ID cannot be empty"
                canAdd = false
            }
        }
        customerMobileNumberEtx.text?.let{
            it.isEmpty().apply {
                if (this) {
                    customerMobileNumberLayout.error = "Mobile number cannot be empty"
                    canAdd = false
                }
                else{
                    val numberLen = it.toString().length
                    if(numberLen<10 ||numberLen>10){
                        customerMobileNumberLayout.error = "Mobile number invalid"
                        canAdd = false
                    }
                }

            }
        }

        if(canAdd) {
            val customer = Customer(
                customerId = customerIdEtx.text!!.toString(),
                name = customerNameEtx.text!!.toString(),
                password = customerPasswordEtx.text!!.toString(),
                gender = gender,
                dob = dob,
                mobile_number = customerMobileNumberEtx.text!!.toString()
            )
            when (mode) {


                Mode.Create -> {
                    model.createNewCustomer(customer)
                    model.join {
                        Toast.makeText(requireContext(), "Customer added", Toast.LENGTH_LONG).show()
                        dialog?.cancel()
                    }
                }
                Mode.Update->{
                    dob= LocalDate.parse(dobBtn.text)
                    if(model.selectedCustomer[0] == customer){
                        dialog?.cancel()
                    }
                    else {
                        model.updateCustomer(model.selectedCustomer[0], customer)
                        model.join {
                            Toast.makeText(requireContext(), "Customer updated", Toast.LENGTH_LONG).show()
                            dialog?.cancel()
                        }
                    }
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()

        if(mode==Mode.Update) {
            val updateCustomer = model.selectedCustomer[0]

            updateCustomer.apply {
                customerNameEtx.setText(this.name)
                customerIdEtx.setText(this.customerId)
                customerPasswordEtx.setText(this.password)
                customerMobileNumberEtx.setText(this.mobile_number)
                dobBtn.text = this.dob.toString()

                when(this.gender){
                    'M'->{
                        customerGenderToggleGrp.check(R.id.admin_customer_male)
                    }
                    'F'->{
                        customerGenderToggleGrp.check(R.id.admin_customer_female)
                    }
                }



                toolbar.title="Update customer"
                toolbar.menu.findItem(R.id.add).title="Update"

            }
        }
    }
}