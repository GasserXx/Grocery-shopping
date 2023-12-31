package com.umega.grocery.shopping

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import com.umega.grocery.utill.SubCategory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.sql.Timestamp

class HomeViewModel (private val navController: NavController,context:Context) : ViewModel() {
    private var repo = Repo(context)
    //handle cart items
    private val cartItemsList = MutableLiveData<List<CartItem>>()

    private val _totalPrice = MutableLiveData("0.00 EGP")
    val totalPrice: LiveData<String> get() = _totalPrice

    private val _address = MutableLiveData<String>()
    private val address: LiveData<String> get() = _address

    private val orderItems = mutableListOf<OrderItem>()
    private var orderId = 0


    private val _logOut = MutableLiveData<Boolean>(false)
    val logOut: LiveData<Boolean> get() = _logOut

    private val _sideItemVisible = MutableLiveData<Boolean>(false)
    val sideItemVisible: LiveData<Boolean> get() = _sideItemVisible


    fun updateTotalPrice() {
        val total = cartItemsList.value?.sumByDouble { it.totalPrice }
        _totalPrice.value = total.toString()+" EGP"
    }
    private  suspend fun placeOrder(){
        viewModelScope.launch {
            val total = cartItemsList.value?.sumByDouble { it.totalPrice }
            Log.i("lolplaceorder",total.toString())
            orderId = repo.insertOrder(Order(null,",",total!!,address.toString(),
                Timestamp(System.currentTimeMillis())
            ))
        }
    }
    private suspend fun placeOrderItems(){
        viewModelScope.launch {

            for (item in cartItemsList.value!!){
                Log.i("lolorder",item.toString())
                orderItems.add(OrderItem(orderId,item.productId,item.price,item.discount,item.itemQuantity))
            }
            repo.insertOrderItems(orderId,orderItems)
        }

    }
    fun placeOrderClick(){
        viewModelScope.launch {
            placeOrder()
            placeOrderItems()
        }
    }

    fun getCartItemsList(): LiveData<List<CartItem>> {
        return cartItemsList
    }
    fun getAllCartItems(){
        repo.getAllCartItems(cartItemsList)
        val total = cartItemsList.value?.sumByDouble { it.totalPrice }
        _totalPrice.value = total.toString()+" EGP"
    }

    //handle sub categories list view data
    private val subCategoriesList = MutableLiveData<List<SubCategory>>()
    fun getSubCategoriesList(): LiveData<List<SubCategory>> {
        return subCategoriesList
    }
    // categories grid view data handle
    private val categoriesList = MutableLiveData<List<Category>>()
    fun getCategoriesList(): LiveData<List<Category>> {
        return categoriesList
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
    // daily deals recycle view data handle
    private val dailyDealsList = MutableLiveData<List<Product>>()
    fun getDailyDealsList(): LiveData<List<Product>> {
        return dailyDealsList
    }
    private fun getAllDailyDeals(){
        repo.getDailyDeals(dailyDealsList)
    }
    // store deals recycle view data handle
    private val storeDealsList = MutableLiveData<List<Product>>()
    fun getStoreDealsList(): LiveData<List<Product>> {
        return storeDealsList
    }
    private fun getAllStoreDeals(){
        repo.getStoreDeals(storeDealsList)
    }
    //TODO handle
    private fun refreshAllDeals() {
        viewModelScope.launch {
            try {
                repo.refreshDailyStoreDeals()
            } catch (networkError: IOException) {
                Log.i("lol", networkError.toString())
            }
        }
    }
    // handle category item click and save category item data
    private val _categoryClickEvent = MutableLiveData<Category>()
    val categoryClickEvent: LiveData<Category> get() = _categoryClickEvent
    fun onCategoryItemClick(category: Category) {
        _categoryClickEvent.value = category
        repo.getAllSubCategoriesByCategory(category.id,subCategoriesList)
        navigateHomeToCategoryDetail()

    }
    private fun navigateHomeToCategoryDetail(){
        navController.navigate(R.id.action_mainPageContainer_to_categoryDetailFragment)
    }
    // back image navigation
    //TODO handle not working
    fun navigateCategoryDetailToHome(){
        //navController.navigate(R.id.action_categoryDetailFragment_to_homeFragment)
        Log.i("lolalhos","hey")
    }
    //TODO handle not working
    fun navigateToResultPage(){
        //navController.navigate(R.id.action_homeFragment_to_resultFragment)
        Log.i("lolalhos","hey")
    }
    fun navigateToSearch(){
        navController.navigate(R.id.action_mainPageContainer_to_searchFragment)
    }
    fun mainListCallBack(option:Int){
        when (option){
            //to order History
            0->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            1->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            2->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            3->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            4->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            5->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            6->navController.navigate(R.id.action_mainPageContainer_to_underConstructionFragment)
            7->logOut()
        }
    }
    private fun logOut(){
        repo.clearPreference()
        _logOut.value = true
    }
    fun hideSideITem(){
        _sideItemVisible.value = false
    }
    fun showSideITem(){
        Log.i("LOL","WE got a HITTTTTT")
        _sideItemVisible.value = true
    }
    init {
        refreshCategoriesAndSubCategories()
        getAllCategories()
        refreshAllDeals()
        getAllDailyDeals()
        getAllStoreDeals()
    }

}