package com.example.stocker.model.helper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.stocker.pojo.Customer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CustomerTableHelper {
    companion object{
        const val customerTableName= "Customer"
        const val id="id"
        const val name="name"
        const val password = "password"
        const val gender="gender"
        const val dob="dob"
        const val mobileNumber="mobileNumber"
    }

    fun createTable(db:SQLiteDatabase){
        db.execSQL("CREATE TABLE $customerTableName($id TEXT PRIMARY KEY, $name TEXT, $password TEXT, $gender TEXT, $dob TEXT, $mobileNumber TEXT)")
    }

    fun getAllData(db:SQLiteDatabase):MutableList<Customer>{

        val customers:MutableList<Customer> = ArrayList()
        val cursor = db.rawQuery("SELECT * FROM $customerTableName",null)


        while(cursor.moveToNext()){

        customers.add(cursorToCustomer(cursor))
        }
        return customers
    }

    fun add(db:SQLiteDatabase,customer:Customer):Boolean{
       return db.insertOrThrow(customerTableName,"null",customerToContentValues(customer)) >0
    }

    fun delete(db:SQLiteDatabase,customers:List<Customer>):Boolean{

        db.beginTransaction()
        var result=false

        for(customer in customers){
            if(db.delete(customerTableName,"id=?", arrayOf(customer.customerId)) >0){
                result=true
            }
            else{
                result=false
                break
            }
        }
        if(result){db.setTransactionSuccessful()}
        db.endTransaction()
        return result
    }

    fun getCustomer(db:SQLiteDatabase,username:String):Customer?{
        val projections = arrayOf(id,name,password,gender,dob, mobileNumber)
        val selection = "$name =?"
        val selectionArgs= arrayOf(username)
        val cursor = db.query(customerTableName,projections,selection,selectionArgs,null,null,null)
        return if(cursor.moveToNext()){
            cursorToCustomer(cursor)
        }
        else null
    }

    fun update(db:SQLiteDatabase,customer:Customer):Boolean{
        return db.replaceOrThrow(customerTableName,null,customerToContentValues(customer)) >0
    }



    private fun cursorToCustomer(cursor: Cursor)=Customer(
        customerId = cursor.getString(0),
        name = cursor.getString(1),
        password = cursor.getString(2) ,
        gender = cursor.getString(3).toCharArray()[0],
        dob = LocalDate.parse(cursor.getString(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        mobile_number = cursor.getString(5)
    )

    private fun customerToContentValues(customer:Customer) = ContentValues().apply {
        put(id,customer.customerId)
        put(name,customer.name)
        put(password,customer.password)
        put(gender,customer.gender.toString())
        put(dob,customer.dob.toString())
        put(mobileNumber,customer.mobile_number)
    }



}