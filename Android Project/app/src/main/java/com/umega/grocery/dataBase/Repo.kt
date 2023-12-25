package com.umega.grocery.dataBase

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.umega.grocery.UserPreference
import com.umega.grocery.dataBase.remote.Remote
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsItemLocal
import com.umega.grocery.utill.DealsType
import com.umega.grocery.utill.FavouriteItemLocal
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

    fun storeUserID(){
        runBlocking {
            userPreference.storeUserID(remote.getUserID(userPreference.getEmail()))
        }
    }
    // refresh categories and sub categories
    suspend fun refreshCategoriesAndSubCategories(){
        withContext(Dispatchers.IO) {
            val allCategoriesFromRemote = remote.getCategories()
            val allSubCategoriesFromRemote = remote.getSubCategories()
            localDatabase.insertCategories(allCategoriesFromRemote)
            localDatabase.insertSubCategories(allSubCategoriesFromRemote)
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
        withContext(Dispatchers.IO) {
            localDatabase.insertFavoriteProducts(remote.getFavorites(userPreference.getUser()))
        }
    }
    fun getAllFavourite(favourites:MutableLiveData<List<FavouriteItemLocal>>){
        favourites.value = localDatabase.getAllFavoriteProducts()
    }
    suspend fun insertFavourite(favourite:MutableLiveData<FavouriteItemLocal>){
        withContext(Dispatchers.IO){
            localDatabase.insertFavoriteProduct(favourite.value)
            remote.addFavorite(userPreference.getUser(), favourite.value!!.productID)
        }
    }
    suspend fun removeFavourite(favourite:MutableLiveData<FavouriteItemLocal>){
        withContext(Dispatchers.IO){
            localDatabase.deleteFavoriteProduct(favourite.value)
            remote.removeFavorite(userPreference.getUser(), favourite.value!!.productID)
        }
    }

    // Daily and store Deals table
    suspend fun refreshDailyStoreDeals(){
        withContext(Dispatchers.IO) {
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


}