package com.example.stocker.view.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.media.MediaCodec.MetricsConstants.MODE
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
import com.example.stocker.view.util.DisplayUtil
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.NumberFormatException
import java.time.LocalDate
import java.util.*

class CustomerDetailsGetterFragment : DialogFragment() {


   private lateinit var customerNameEtx:TextInputEditText
   private lateinit var customerNameLayout:TextInputLayout
    private lateinit var customerIdEtx:TextInputEditText
    private lateinit var customerIdLayout:TextInputLayout
    private lateinit var customerPasswordEtx:TextInputEditText
    private lateinit var customerPasswordLayout:TextInputLayout
    private lateinit var customerMobileNumberEtx:TextInputEditText
    private lateinit var customerMobileNumberLayout:TextInputLayout
    private lateinit var maleBtn:MaterialButton
    private lateinit var femaleBtn:MaterialButton
    private lateinit var dobBtn:MaterialButton
    private lateinit var addBtn:MaterialButton
    private lateinit var cancelBtn:MaterialButton
    private var dob = LocalDate.now()
    private var gender:Char='M'
    private val model:AdminViewModel by activityViewModels()

    private var mode = Mode.Create




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

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialog!!.setCancelable(false)
        val size = DisplayUtil.getDisplaySize(activity!!)
        val height = size.x
        val width = size.y
        dialog!!.window!!.setLayout(width, (height/1.5).toInt())



        customerIdEtx = view.findViewById(R.id.admin_enter_customer_id)
        customerIdLayout = view.findViewById(R.id.admin_enter_customer_id_layout)
        customerPasswordEtx = view.findViewById(R.id.admin_enter_customer_password)
        customerPasswordLayout = view.findViewById(R.id.admin_enter_customer_password_layout)
        customerNameEtx = view.findViewById(R.id.admin_enter_customer_name)
        customerNameLayout = view.findViewById(R.id.admin_enter_customer_name_layout)
        customerMobileNumberEtx = view.findViewById(R.id.admin_enter_customer_mobile_number)
        customerMobileNumberLayout = view.findViewById(R.id.admin_enter_customer_mobile_number_layout)
        maleBtn = view.findViewById(R.id.admin_customer_male)
        femaleBtn = view.findViewById(R.id.admin_customer_female)
        cancelBtn = view.findViewById(R.id.admin_cancel_btn)
        addBtn = view.findViewById(R.id.admin_add_btn)
        dobBtn = view.findViewById(R.id.admin_customer_dob_btn)


        model.resultStatus.observe(this,{status->
            status?.let {
                if(!status.isHandled) {
                    Snackbar.make(view, status.msg, Snackbar.LENGTH_INDEFINITE).setAction("close") {
                        status.isHandled = true
                        dialog!!.cancel()
                    }.show()
                }
            }

        })

        customerMobileNumberEtx.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                try{
                    s!!.toString().toInt()
                }
                catch(e:NumberFormatException){
                    customerMobileNumberLayout.error = "number invalid"
                }
                when {

                    s!!.length > customerMobileNumberLayout.counterMaxLength -> customerMobileNumberLayout.error = "Mobile number cannot exceed 10 digits"
                    s.isEmpty() && customerMobileNumberLayout.error!=null ->customerMobileNumberLayout.error = null
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
            val datePicker = DatePickerDialog(activity!!,
                { _, year, month, dayOfMonth ->
                    dob = LocalDate.of(year,month+1, dayOfMonth)
                    dobBtn.text=dob.toString()
                },
                year,month,day)
            datePicker.show()
        }

        cancelBtn.setOnClickListener {
            dialog?.cancel()
        }

        maleBtn.setOnClickListener {
            gender='M'
            it.setBackgroundColor(context!!.getColor(R.color.black))
            femaleBtn.setBackgroundColor(context!!.getColor(R.color.design_default_color_on_primary))
        }
        femaleBtn.setOnClickListener {
            gender='F'
            it.setBackgroundColor(context!!.getColor(R.color.black))
            maleBtn.setBackgroundColor(context!!.getColor(R.color.design_default_color_on_primary))
        }

        addBtn.setOnClickListener {
            var canAdd=true
            customerNameEtx.text?.isEmpty().apply {
                if(this!!) {
                    customerNameLayout.error = "name cannot be empty"
                    canAdd = false
                }
            }
            customerPasswordEtx.text?.isEmpty().apply{
                if(this!!) {
                    customerPasswordLayout.error = "password cannot be empty"
                    canAdd = false
                }
            }
            customerIdEtx.text?.isEmpty().apply{
                if(this!!) {
                    customerIdLayout.error = "Id cannot be empty"
                    canAdd = false
                }
            }
            customerMobileNumberEtx.text?.let{
                it.isEmpty().apply {
                if (this) {
                    customerMobileNumberLayout.error = "mobile number cannot be empty"
                    canAdd = false
                }
                else{
                    val numberLen = it.toString().length
                    if(numberLen<10 ||numberLen>10){
                        customerMobileNumberLayout.error = "mobile number invalid"
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
                       Toast.makeText(context!!, "Customer added", Toast.LENGTH_LONG).show()
                       dialog?.cancel()
                   }
               }
                    Mode.Update->{
                        model.updateCustomer(model.selectedCustomer[0],customer)
                        model.join {
                            Toast.makeText(context!!, "Customer updated", Toast.LENGTH_LONG).show()
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

                if (this.gender == 'M') {
                    maleBtn.setBackgroundColor(context!!.getColor(R.color.black))
                    femaleBtn.setBackgroundColor(context!!.getColor(R.color.design_default_color_on_primary))
                } else {
                    femaleBtn.setBackgroundColor(context!!.getColor(R.color.black))
                    maleBtn.setBackgroundColor(context!!.getColor(R.color.design_default_color_on_primary))

                }
                addBtn.text="update"

            }
        }
    }

}