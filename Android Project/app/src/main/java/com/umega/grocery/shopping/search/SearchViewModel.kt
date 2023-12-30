package com.umega.grocery.shopping.search

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Keys

class SearchViewModel(private val navController: NavController) :ViewModel() {

    private val _ids = MutableLiveData<ArrayList<Int>>(arrayListOf())
    val ids: LiveData<ArrayList<Int>>
        get() = _ids

    private val _searchResults = MutableLiveData<MutableList<String>>(mutableListOf())
    val searchResults: LiveData<MutableList<String>>
        get() = _searchResults

    val searchText = MutableLiveData("")

    private var repo:Repo? = null

    fun setRepo(repoObj:Repo){repo = repoObj}

    fun updateTexts(){
        repo!!.searchText(searchText.value!!, _searchResults)
    }
    fun searchTextItem(searchVal: String){
        searchText.value = searchVal
        search()
    }
    fun search(){
        if (searchText.value == "")
            return
        //get all ids
        repo!!.searchProducts(searchText.value!!, _ids)
    }

    fun afterSearchCallback(){
        navigateToResult(ids.value!!, searchText.value!!)
    }
    //navigation
    fun navigateUp(){
        navController.navigateUp()
    }
    private fun navigateToResult(ids:ArrayList<Int>, searchString:String){
        val bundle = Bundle().apply {
            putString(Keys.result_title_bundle_key, searchString)
            putIntegerArrayList(Keys.result_ids_bundle_key,ids)
        }
        navController.navigate(R.id.action_searchFragment_to_resultContainer,bundle)
    }

}