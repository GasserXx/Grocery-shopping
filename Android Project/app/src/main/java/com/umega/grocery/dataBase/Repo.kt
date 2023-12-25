package com.umega.grocery.dataBase

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.umega.grocery.UserPreference
import android.util.Log
import com.umega.grocery.dataBase.remote.Remote
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsItemLocal
import com.umega.grocery.utill.DealsType
import com.umega.grocery.utill.FavouriteItemLocal
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.SubCategory
import com.umega.grocery.utill.User
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class Repo(context: Context) {
    private val remote = Remote()
    private val localDatabase = LocalDatabaseHelper(context)
    private val userPreference = UserPreference(context)
    fun authentication(user: User, response:MutableLiveData<Int>){
        runBlocking {
            response.value = remote.authentication(user)
        }
    }

    fun registerUser(user: User, response:MutableLiveData<Int>){
        runBlocking {
            response.value = remote.registerUser(user)
        }
    }

    fun storeEmail(email: String) {
        runBlocking {
            userPreference.storeEmail(email)
        }
    }
    fun storeUserID(){
        runBlocking {
            userPreference.storeUserID(remote.getUserID(userPreference.getEmail()))
        }
    }
    // refresh categories and sub categories
    suspend fun refreshCategoriesAndSubCategories(){
        try{
            runBlocking {
                localDatabase.clearCategoriesAndSubCategories()
                var allCategoriesFromRemote = remote.getCategories()
                Log.i("lol1",allCategoriesFromRemote.toString())
                Log.i("lol5","bdan")
                allCategoriesFromRemote = listOf(Category(1,"Grocery"),
                    Category(2,"Beverages"),
                    Category(3,"Chilled"),
                    Category(4,"Drinks"),
                    Category(5,"Fish"),
                    Category(6,"Frozen Food"),
                    Category(7,"Fruits"),
                    Category(8,"Home Ware"),
                    Category(9,"House Hold"),
                    Category(10,"Meat"),
                    Category(11,"Pharmacy"),
                    Category(12,"Vegetables")).toMutableList()
                val allSubCategoriesFromRemote = remote.getSubCategories()
                localDatabase.insertCategories(allCategoriesFromRemote)
                localDatabase.insertSubCategories(allSubCategoriesFromRemote)
            }
        }catch (e:Exception){
            Log.i("lol",e.toString())
        }

    }
    // get all categories  and subCategories
    fun getAllCategories(categories:MutableLiveData<List<Category>>){
        categories.value = localDatabase.getAllCategories()
    }
    fun getAllSubCategoriesByCategory(categoryId:Int,categories:MutableLiveData<List<SubCategory>>){
        categories.value = localDatabase.getAllSubcategoriesByCategory(categoryId)
    }
    /*TODO make function to get products from remote by subCategory id*/

    // favourite table
    suspend fun refreshFavourite(){
        runBlocking {
            localDatabase.insertFavoriteProducts(remote.getFavorites(userPreference.getUser()))
        }
    }
    fun getAllFavourite(favourites:MutableLiveData<List<FavouriteItemLocal>>){
        favourites.value = localDatabase.getAllFavoriteProducts()
    }
    suspend fun insertFavourite(favourite:MutableLiveData<FavouriteItemLocal>){
        runBlocking{
            localDatabase.insertFavoriteProduct(favourite.value)
            remote.addFavorite(userPreference.getUser(), favourite.value!!.productID)
        }
    }
    suspend fun removeFavourite(favourite:MutableLiveData<FavouriteItemLocal>){
        runBlocking{
            localDatabase.deleteFavoriteProduct(favourite.value)
            remote.removeFavorite(userPreference.getUser(), favourite.value!!.productID)
        }
    }

    // Daily and store Deals table
    suspend fun refreshDailyStoreDeals(){
        runBlocking {
            localDatabase.insertDailyDeals(remote.getDeals(DealsType.Daily))
            localDatabase.insertStoreDeals(remote.getDeals(DealsType.Store))
        }
    }
    fun getDailyDeals(dailyDeals:MutableLiveData<List<DealsItemLocal>>){
        dailyDeals.value = localDatabase.getAllDailyDeals()
    }
    fun getStoreDeals(storeDeals:MutableLiveData<List<DealsItemLocal>>){
        storeDeals.value = localDatabase.getAllStoreDeals()
    }
    // brands table
    suspend fun refreshBrands(){
        withContext(Dispatchers.IO) {
            localDatabase.insertBrands(remote.getBrands())
        }
    }

    fun getUserID():Int {
        val id:Int
        runBlocking {
        id = userPreference.getUser()
        }
        return id
    }
    // orders and orders Items tables
    suspend fun refreshOrderAndOrderItemsTables(){
        runBlocking {
            val orderItems = mutableListOf<OrderItem>()
            val orders = remote.getOrders(userPreference.getUser())
            localDatabase.insertOrders(orders)
            for (order in orders){
                localDatabase.insertOrderItems(remote.getOrderItems(order.id!!))
            }
        }
    }
    suspend fun insertOrder(order:MutableLiveData<Order>,orderItems:MutableLiveData<List<OrderItem>>){
        runBlocking{
            localDatabase.insertOrder(order.value!!)
            localDatabase.insertOrderItems(orderItems.value!!)
            remote.placeOrder(userPreference.getUser(), order.value!!)
          //  remote.it(userPreference.getUser(), order.value!!)
        }
    }
    // cart tables
    fun getAllCartItems(cartItems:MutableLiveData<List<CartItem>>){
        cartItems.value = localDatabase.getAllCartItems()
    }
    fun insertCartItem(productID: Int, quantity: Int) {
        localDatabase.insertCartItem(productID,quantity)
    }
    fun updateCartItemQuantity(productID: Int, newQuantity: Int) {
        localDatabase.updateCartItemQuantity(productID,newQuantity)
    }
    fun deleteCartItem(productID: Int) {
        localDatabase.deleteCartItem(productID)
    }
    //TODO Address table function
}