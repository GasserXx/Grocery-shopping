package com.umega.grocery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.auth.LoginViewModelFactory
import com.umega.grocery.dataBase.LocalDatabaseHelper
import com.umega.grocery.databinding.ActivityMainBinding
import com.umega.grocery.shopping.HomeViewModel
import com.umega.grocery.shopping.HomeViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController:NavController
    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(navController,this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        //handle logout
        viewModel.logOut.observe(this){
            if (it)
                endActivity()
        }

    }
    private fun endActivity(){
        // Create an Intent to start the new activity
        val intent = Intent(this, LoginActivity::class.java)
        // Start the new activity
        startActivity(intent)
        // Finish the current activity to close it
        finish()
    }
}
