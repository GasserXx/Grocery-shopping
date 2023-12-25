package com.umega.grocery.shopping.fragments

import android.content.Context
import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Category
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel (private val navController: NavController,context:Context) : ViewModel() {
    private var repo = Repo(context)
    private val categoriesList = MutableLiveData<List<Category>>()
    fun getItemList(): LiveData<List<Category>> {
        return categoriesList
    }
    fun swapLinearLayout(){

    }
    private fun refreshCategoriesAndSubCategories() {
        viewModelScope.launch {
            try {
                repo.refreshCategoriesAndSubCategories()
            } catch (networkError: IOException) {
                Log.i("lol", networkError.toString())
            }
        }
    }
    private fun getAllCategories(){
        repo.getAllCategories(categoriesList)
    }
    init {
        Log.i("lol","hi from view model")
        refreshCategoriesAndSubCategories()
        getAllCategories()
    }

}