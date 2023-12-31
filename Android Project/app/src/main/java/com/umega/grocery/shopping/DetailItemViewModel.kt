package com.umega.grocery.shopping

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Product

class DetailItemViewModel(private val navController: NavController, context: Context) : ViewModel() {
    private var repo = Repo(context)
    private val _productLiveData = MutableLiveData<Product>()
    val productLiveData: LiveData<Product>
        get() = _productLiveData
    private val _productQuantity = MutableLiveData<Int>()
    val productQuantity: LiveData<Int>
        get() = _productQuantity
    fun initialization(productValue: Product) {
        _productLiveData.value = productValue
    }
    fun increaseQuantity() {
        val currentQuantity = _productQuantity.value ?: 0
        _productQuantity.value = currentQuantity + 1
    }
    fun decreaseQuantity() {
        val currentQuantity = _productQuantity.value ?: 0
        if(currentQuantity - 1 >=0){
            _productQuantity.value = currentQuantity - 1
        }
    }
    fun addToCart(){
        repo.insertCartItem(productLiveData.value!!.id, productQuantity.value!!)
        navController.navigate(R.id.action_detailItemFragment_to_mainPageContainer)
    }

}