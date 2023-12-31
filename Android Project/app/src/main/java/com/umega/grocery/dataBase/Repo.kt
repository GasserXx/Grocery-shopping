package com.umega.grocery.dataBase

import ImageHandle
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.umega.grocery.UserPreference
import android.util.Log
import com.umega.grocery.dataBase.remote.Remote
import com.umega.grocery.utill.Brand
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsType
import com.umega.grocery.utill.FavouriteItemLocal
import com.umega.grocery.utill.Filter
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
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
    private val userPreference = UserPreference.getInstance()
    private val imageHandle = ImageHandle(context)
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
    //retrieve products onDemand fn
    private fun retrieveProductsRemotely(productsIDs: List<Int>, filter: Filter = Filter()):MutableList<Product>{
        val products:MutableList<Product>
        runBlocking {

            Log.i("LOL", "ATTEMPTING CONNECTION")
            products = remote.getProducts(productsIDs, filter)
            Log.i("LOL",products.toString())
            cacheProductsImages(products)

            localDatabase.insertProducts(products)
        }
        return products
    }

    fun clearPreference(){
        runBlocking {
            userPreference.clearPreference()
        }
    }
    //request products from cached
    fun retrieveProducts(
        productsIDs: MutableList<Int>,
        filter: Filter,
        flag: MutableLiveData<MutableList<Product>>
    ) {
        val products:MutableList<Product>
        val missingProducts:MutableList<Int>

        runBlocking {
            localDatabase.getProducts(productsIDs,filter).apply {
                products = this.first
                missingProducts = this.second
            }
        }
        Log.i("LOL", "Got out of local db")
        if (missingProducts.isNotEmpty())
            runBlocking {
                Log.i("LOL", "Entering Remote")
                products.addAll(retrieveProductsRemotely(missingProducts, filter))
                flag.value = products
                Log.i("LOL", "Got out of remote db")
            }
        else
        //on change of the liveData it means that the required products are in the local DB
        //flag indicating all data retrieved
                flag.value = products
    }
    fun getBrandById(ids:MutableList<Int>): MutableList<Brand> {
        val brands :MutableList<Brand> = mutableListOf()
        ids.forEach { brands.add(localDatabase.getBrandById(it)!!)}
        return brands
    }

    fun getAllStoredOrders():MutableList<Order>{
        return localDatabase.getAllOrders()
    }

    // refresh categories and sub categories
    suspend fun refreshCategoriesAndSubCategories(){
        try{
            runBlocking {
                localDatabase.clearCategoriesAndSubCategories()
                val allCategoriesFromRemote = listOf(Category(1,"HouseHold"),
                    Category(2,"Grocery"),Category(3,"Drinks"),
                    Category(4,"Chilled"),Category(5,"Beverages"),
                    Category(6,"Pharmacy"),Category(7,"Frozen Food"),
                    Category(8,"Vegetables"),Category(9,"Meat"),
                    Category(10,"Fish"),Category(11,"HomeWare"),
                    Category(12,"Fruits"))
                val allSubCategoriesFromRemote = remote.getSubCategories()
                Log.i("lolaliibrahim",allSubCategoriesFromRemote.toString())
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
    suspend fun getAllProductsIdsBySubCategoryId(subCategoryId: Int):List<Int>{
        var productsIds:List<Int>
        runBlocking {
             productsIds =remote.getProductsBySubCategoryID(subCategoryId)
        }
        return productsIds
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
    fun getDealsProductIds(type:DealsType,ids:MutableLiveData<List<Int>>){
        if(type == DealsType.Daily){
            ids.value = localDatabase.getDailyDealsProductsIds()
        }else{
            ids.value = localDatabase.getStoreDealsProductsIds()
        }
    }
    suspend fun refreshDailyStoreDeals(){
        runBlocking {
            val allDailyDealsFromRemote = remote.getDeals(DealsType.Daily)
            val allStoreDealsFromRemote = remote.getDeals(DealsType.Store)
            Log.i("loldaily",allDailyDealsFromRemote.toString())
            localDatabase.insertDailyDeals(allDailyDealsFromRemote)
            localDatabase.insertStoreDeals(allStoreDealsFromRemote)
            val lol = localDatabase.getDailyStoreProductIds()
            retrieveProductsRemotely(lol)

            localDatabase.updateProductDiscounts(allDailyDealsFromRemote,allStoreDealsFromRemote)

        }
    }
    fun getDailyDeals(dailyDeals:MutableLiveData<List<Product>>){
        runBlocking {
            dailyDeals.value = localDatabase.getAllDailyDeals()
            Log.i("loldata",localDatabase.getAllDailyDeals().toString())
        }

    }
    fun getStoreDeals(storeDeals:MutableLiveData<List<Product>>){
        storeDeals.value = localDatabase.getAllStoreDeals()
        Log.i("loldatastore",localDatabase.getAllStoreDeals().toString())
        Log.i("lol27", localDatabase.getProduct(27).toString())
    }
    // brands table
    suspend fun refreshBrands(){
        withContext(Dispatchers.IO) {
            val brands = remote.getBrands()
            localDatabase.insertBrands(brands)
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
    suspend fun insertOrder(order:Order):Int{
        var orderId: Int? = 0
        runBlocking{
            try{
                //TODO add user preference id
                orderId = remote.placeOrder(userPreference.getUser(), order)
                Log.i("lolorder",orderId.toString())
            }catch (e:Exception){
                Log.i("lolplaceorder",orderId.toString())
            }
            order.id = orderId
            localDatabase.insertOrder(order)
        }
        return orderId!!
    }
    suspend fun insertOrderItems(orderID:Int, orderItems: MutableList<OrderItem>){
        runBlocking {
            Log.i("LOL","orderitems size ${orderItems.size}")
            remote.placeOrderItems(orderID,orderItems)
            localDatabase.insertOrderItems(orderItems)
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
    fun deleteAllCArtItems(){
        localDatabase.deleteAllCartItems()
    }
    //TODO Address table function
    //download download images
    private suspend fun cacheProductsImages(products:MutableList<Product>){
        runBlocking {
            for(product in products){
                Log.i("LOLO",product.imgName)
                val imageUrl = "https://raw.githubusercontent.com/GasserXx/Grocery-shopping/main/UI%20Design/Images/webp/"+product.imgName

                Log.i("LOLO","Attempting Caching Image")
                val response = imageHandle.cacheImage(imageUrl,product.imgName)
                Log.i("LOLO","$response cached image callback")
            }
        }
    }
    fun getOrderByID(orderId:Int):Order{
        return localDatabase.getOrderById(orderId)!!
    }
    fun getOrderItems(orderId:Int): MutableList<OrderItem> {
        return localDatabase.getOrderItemsByOrderId(orderId = orderId)
    }
    fun searchText(keyWord: String, searchResults: MutableLiveData<MutableList<String>>) {
        runBlocking {
            withContext(Dispatchers.Unconfined){
                    searchResults.postValue(remote.searchTexts(keyWord))
            }
        }
    }
    fun searchProducts(keyWord: String, searchResults: MutableLiveData<ArrayList<Int>>) {
        runBlocking {
            withContext(Dispatchers.Unconfined){
                searchResults.postValue(remote.searchProducts(keyWord))
            }
        }
    }
}