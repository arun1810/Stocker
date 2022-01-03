package com.example.stocker.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.stocker.R
import com.example.stocker.pojo.Customer
import com.example.stocker.pojo.Stock
import com.example.stocker.viewmodel.AdminViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val model = ViewModelProvider(this)[AdminViewModel::class.java]

        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = (supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment).navController
        bottomNavigationView.setupWithNavController(navController)




    }

    private fun data(model:AdminViewModel){
        model.createNewCustomer(Customer("0A","A","pass",'F', LocalDate.now(),"1234567890"))
        model.createNewCustomer(Customer("0B","B","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0C","C","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0D","D","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0E","E","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0F","F","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0G","G","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0H","H","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0I","I","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0J","J","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0K","K","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0L","L","pass",'F', LocalDate.now(),"1234567890"))

        model.createNewCustomer(Customer("0M","M","pass",'F', LocalDate.now(),"1234567890"))


        model.addStock(Stock("#A","A",123,1003,2))
        model.addStock(Stock("#B","B",12,10234,2))
        model.addStock(Stock("#C","C",1283,12340,2))
        model.addStock(Stock("#D","D",121,23400,2))
        model.addStock(Stock("#E","E",125,2341,2))
        model.addStock(Stock("#F","F",1266,1234,34))
        model.addStock(Stock("#I","G",1233,23412,2))
        model.addStock(Stock("#J","H",1278,2342,2))
        model.addStock(Stock("#K","I",1,112310,2))
        model.addStock(Stock("#L","J",1245,1212,2))
        model.addStock(Stock("#M","K",13124,12313,2))
        model.addStock(Stock("#N","L",12323,234231,2))
        model.addStock(Stock("#O","M",126,124123,2))
        model.addStock(Stock("#P","N",1235,123131,2))
        model.addStock(Stock("#Q","O",1457,456450,2))
        model.addStock(Stock("#R","P",1234,34535,2))
        model.addStock(Stock("#S","Q",12334,352,2))
        model.addStock(Stock("#T","R",156,345,2))
        model.addStock(Stock("#U","S",2353,5676,2))
        model.addStock(Stock("#V","T",157,5645,2))
        model.addStock(Stock("#W","U",34523,5675,2))
        model.addStock(Stock("#X","V",5634,34,2))
        model.addStock(Stock("#Y","W",12343,6456,2))
        model.addStock(Stock("#Z","X",234325,45646,2))

    }
}