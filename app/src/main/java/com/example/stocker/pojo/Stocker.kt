package com.example.stocker.pojo

class Stocker private constructor(){
    val customer = Customer[0]
    companion object{
        private var Customer: Array<Customer?> = arrayOf(null)
        private var Instance:Stocker?=null


        fun createInstance(customer:Customer){
           Customer[0]=customer
           Instance = Stocker()
       }

        fun getInstance()= Instance

        fun logout(){
            Instance=null
            Customer[0] = null
        }
    }


}
