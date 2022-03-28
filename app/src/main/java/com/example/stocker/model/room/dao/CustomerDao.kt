package com.example.stocker.model.room.dao

import androidx.room.*
import com.example.stocker.pojo.Customer

@Dao

abstract class CustomerDao {

    @Query("SELECT * FROM customer")
    abstract fun getAllData(): MutableList<Customer>

    @Insert
    abstract fun add(customer:Customer)

    @Delete
    abstract fun delete(customer: Customer)

    @Transaction
    open fun deleteListOfCustomer(customers:List<Customer>){
        for(cus in customers){
            delete(cus)
        }
    }

    @Query("SELECT * FROM customer WHERE name = :name")
    abstract fun getCustomer(name :String):Customer?

    @Query("UPDATE customer SET id =:customerId,name=:name,password=:password,gender=:gender,dob=:dob,mobileNumber=:mobileNumber WHERE id =:oldId")
    abstract fun update(customerId:String,name:String,password:String,gender:Char,dob:String,mobileNumber:String,oldId:String,)


}