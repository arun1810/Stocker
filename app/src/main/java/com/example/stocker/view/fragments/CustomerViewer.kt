package com.example.stocker.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class CustomerViewer : DialogFragment() {
    lateinit var customer:Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppTheme_FullScreenDialog)

        customer=(arguments?.get("data") as Pair<*,*>).second as Customer
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar:MaterialToolbar = view.findViewById(R.id.toolbar)
        val customerImage:ShapeableImageView = view.findViewById(R.id.customer_img)
        val customerName:MaterialTextView=view.findViewById(R.id.customer_name)
        val customerId:MaterialTextView=view.findViewById(R.id.customer_id)
        val gender:MaterialTextView=view.findViewById(R.id.customer_gender)
        val dob:MaterialTextView = view.findViewById(R.id.customer_dob)
        val mobile:MaterialTextView = view.findViewById(R.id.customer_mobile)

        dialog?.setCancelable(false)

        toolBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        toolBar.title="Customer"
        toolBar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        customerName.text=customer.name
        customerId.text=customer.customerId
        dob.text="DOB: ${customer.dob}"
        mobile.text="Mobile: ${customer.mobile_number}"


        customer.gender.let{

            when(it){
                'M'->{
                    customerImage.setImageResource(R.drawable.ic_male_avatar_vector)
                    gender.text="Gender: Male"
                }
                'F'->{
                    customerImage.setImageResource(R.drawable.ic_female_avator_vector)
                    gender.text="Gender: Female"
                }
            }
        }



    }

}