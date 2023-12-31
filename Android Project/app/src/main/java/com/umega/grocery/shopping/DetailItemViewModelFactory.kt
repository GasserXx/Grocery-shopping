package com.umega.grocery.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController

class DetailItemViewModelFactory (private val navController: NavController, private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailItemViewModel(navController,context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}