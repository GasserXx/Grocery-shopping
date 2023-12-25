package com.umega.grocery.shopping.fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.umega.grocery.auth.LoginViewModel

class HomeViewModelFactory(private val navController: NavController,private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(navController,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
