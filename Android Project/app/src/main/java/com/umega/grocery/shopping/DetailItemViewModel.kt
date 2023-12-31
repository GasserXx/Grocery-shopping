package com.umega.grocery.shopping

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.dataBase.Repo

class DetailItemViewModel(private val navController: NavController, context: Context) : ViewModel() {
    private var repo = Repo(context)

}