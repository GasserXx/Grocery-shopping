package com.umega.grocery.shopping.main
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsType
import com.umega.grocery.utill.Keys
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import com.umega.grocery.utill.SubCategory
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.sql.Timestamp

class HomeViewModel (private val navController: NavController,context:Context) : ViewModel() {
    private val _logOut = MutableLiveData<Boolean>(false)
    val logOut: LiveData<Boolean> get() = _logOut

    val navigate_to_result = MutableLiveData(false)

    private val _sideItemVisible = MutableLiveData<Boolean>(false)
    val sideItemVisible: LiveData<Boolean> get() = _sideItemVisible

    private var repo = Repo(context)
    //handle cart items
    private val cartItemsList = MutableLiveData<List<CartItem>>()

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _toastMessage: MutableLiveData<String> = MutableLiveData("")
    val toastMessage: LiveData<String>
        get() = _toastMessage
    fun getCartItemsList(): LiveData<List<CartItem>> {
        return cartItemsList
    }
    fun getAllCartItems(){
        repo.getAllCartItems(cartItemsList)
        val total = cartItemsList.value?.sumByDouble { it.totalPrice }
        totalPrice.value = total.toString()+" EGP"
    }
    val totalPrice = MutableLiveData("0.00 EGP")
    fun updateTotalPrice() {
        val total = cartItemsList.value?.sumByDouble { it.totalPrice }
        totalPrice.value = total.toString()+" EGP"
    }
    private val _address = MutableLiveData<String>()
    private val address: LiveData<String> get() = _address
    var orderId = 0
    private  suspend fun placeOrder(){
        showLoading()
        viewModelScope.launch {
            val total = cartItemsList.value?.sumByDouble { it.totalPrice }
            Log.i("lolplaceorder",total.toString())
            orderId = repo.insertOrder(Order(null,"",total!!,address.value.toString(),
                Timestamp(System.currentTimeMillis())
            ))
            Log.i("lolplaceorder",orderId.toString())
            placeOrderItems(cartItemsList.value!!)
        }
        runBlocking {
            repo.refreshOrderAndOrderItemsTables()
        }
    }

    private val orderItems = mutableListOf<OrderItem>()
    private suspend fun placeOrderItems(cartItemsList:List<CartItem>){
        viewModelScope.launch {

            for (item in cartItemsList){
                orderItems.add(OrderItem(orderId,item.productId,item.price,item.discount,item.itemQuantity))
            }
            //TODO check why no items passed
            Log.i("LOL", "CartItems with size ${cartItemsList.size} to passed with size ${orderItems.size}")
            repo.insertOrderItems(orderId,orderItems)
            navigate_to_result.value = true
        }
    }
    //TODO add navigation and bundle as you like note that there are variable called orderId
    fun placeOrderClick(){
        viewModelScope.launch {
            placeOrder()

            repo.deleteAllCArtItems()
            cartItemsList.value = mutableListOf()
            totalPrice.value = ""
        }
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
    private val dailyDealsIdsList = MutableLiveData<List<Int>>()
    fun getDailyDealsIdsList(): LiveData<List<Int>> {
        return dailyDealsIdsList
    }
    private val storeDealsIdsList = MutableLiveData<List<Int>>()

    fun getStoreDealsIdsList(): LiveData<List<Int>> {
        return storeDealsIdsList
    }

    private val dailyDealsList = MutableLiveData<List<Product>>()
    fun getDailyDealsList(): LiveData<List<Product>> {
        return dailyDealsList
    }
    private fun getAllDailyDeals(){
        repo.getDailyDeals(dailyDealsList)
        repo.getDealsProductIds(DealsType.Daily,dailyDealsIdsList)
    }
    // store deals recycle view data handle
    private val storeDealsList = MutableLiveData<List<Product>>()
    fun getStoreDealsList(): LiveData<List<Product>> {
        return storeDealsList
    }
    private fun getAllStoreDeals(){
        repo.getStoreDeals(storeDealsList)
        repo.getDealsProductIds(DealsType.Store,storeDealsIdsList)
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
    suspend fun navigateCategoryDetailToResultPage(subCategory: SubCategory){
        viewModelScope.launch {
            val subCategoryProductsIdsFromRemote = repo.getAllProductsIdsBySubCategoryId(subCategory.id)
            val dailyDealsIdsArrayList: ArrayList<Int> = ArrayList()
            dailyDealsIdsList.value?.let {
                dailyDealsIdsArrayList.clear()
                dailyDealsIdsArrayList.addAll(it)
            }
            val bundle = bundleOf(
                Keys.result_title_bundle_key to subCategory.name,
                Keys.result_ids_bundle_key to subCategoryProductsIdsFromRemote
            )
            navController.navigate(R.id.action_categoryDetailFragment_to_resultContainer, bundle)
        }
    }
    // back image navigation
    //TODO handle not working
    fun navigateUp(){
        navController.navigateUp()
    }
    fun navigateToResultPageFromDailyDeals(){
        val dailyDealsIdsArrayList: ArrayList<Int> = ArrayList()
        dailyDealsIdsList.value?.let {
            dailyDealsIdsArrayList.clear()
            dailyDealsIdsArrayList.addAll(it)
            }

        val bundle = bundleOf(
            Keys.result_title_bundle_key to "Daily Deals",
            Keys.result_ids_bundle_key to dailyDealsIdsArrayList
        )
        navController.navigate(R.id.action_homeFragment_to_resultFragment,bundle)
    }
    fun navigateToResultPageFromStoreDeals(){
        val storeDealsIdsArrayList: ArrayList<Int> = ArrayList()
        dailyDealsIdsList.value?.let {
            storeDealsIdsArrayList.clear()
            storeDealsIdsArrayList.addAll(it)
        }
        val bundle = bundleOf(
            Keys.result_title_bundle_key to "Store Deals",
            Keys.result_ids_bundle_key to storeDealsIdsArrayList
        )
        navController.navigate(R.id.action_homeFragment_to_resultFragment,bundle)
    }

    fun navigateSubCategoryToSearch(){
        navController.navigate(R.id.action_categoryDetailFragment_to_searchFragment)
    }
    fun navigateToSearch(){
        navController.navigate(R.id.action_mainPageContainer_to_searchFragment)
    }

    fun navigateToDetailOrder() {
        val order = repo.getOrderByID(orderId)
        val bundle = Bundle().apply {
            putInt(Keys.order_id_bundle_key,order.id?:-1)
            putDouble(Keys.total_price_bundle_key, order.totalPrice)
            putString(Keys.address_bundle_key,order.address)
            putLong(Keys.date_bundle_key, order.date!!.time)
        }
        navController.navigate(R.id.action_mainPageContainer_to_orderDetailFragment, bundle)
    }

    fun mainListCallBack(option:Int){
        when (option){
            //to order History
            0->navController.navigate(R.id.action_mainPageContainer_to_orderHistoryFragment)
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

    //Loading functions
    fun showLoading() {
        _loading.value = true
    }

    fun hideLoading() {
        _loading.value = false
    }
    fun showMessage(message:String){
        _toastMessage.value = message
    }
    init {
        refreshCategoriesAndSubCategories()
        getAllCategories()
        refreshAllDeals()
        getAllDailyDeals()
        getAllStoreDeals()
    }

}