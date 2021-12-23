package com.example.stocker.pojo

import java.time.LocalDate

data class Customer(val customerId:String,val name:String,val password:String,val gender:Char,val dob:LocalDate,val mobile_number:String)
