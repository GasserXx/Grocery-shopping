package com.umega.grocery.shopping.fragments

import ImageHandle
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
import com.umega.grocery.utill.DealsItemLocal
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException

class HomeViewModel (private val navController: NavController,context:Context) : ViewModel() {
    private var repo = Repo(context)
    private val categoriesList = MutableLiveData<List<Category>>()
    private val dealsList = MutableLiveData<List<DealsItemLocal>>()
    val imageHandle:ImageHandle = ImageHandle(context)
    //use in observer
    fun getCategoriesList(): LiveData<List<Category>> {
        return categoriesList
    }
    fun getDealsList(): LiveData<List<DealsItemLocal>> {
        return dealsList
    }
    private fun refreshCategoriesAndSubCategories() {
        viewModelScope.launch {
            try {
                repo.refreshCategoriesAndSubCategories()
                imageHandle.cacheImage("https://picsum.photos/200","aaaa")
            } catch (networkError: IOException) {
                Log.i("lol", networkError.toString())
            }
        }
    }
    private fun getAllCategories(){
        repo.getAllCategories(categoriesList)
    }
    private fun getAllDeals(){
        repo.getDailyDeals(dealsList)

    }
    init {
        Log.i("lol","hi from view model")
        refreshCategoriesAndSubCategories()
        getAllCategories()
        getAllDeals()
    }

}