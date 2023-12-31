package com.umega.grocery.dataBase.remote

import android.util.Log
import com.umega.grocery.utill.Address
import com.umega.grocery.utill.Brand
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsItem
import com.umega.grocery.utill.DealsType
import com.umega.grocery.utill.FavoriteItem
import com.umega.grocery.utill.Filter
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import com.umega.grocery.utill.SubCategory
import com.umega.grocery.utill.User
import com.umega.grocery.utill.VerbalSortingType
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import java.sql.ResultSet

/*
* Remote Schema: https://showme.redstarplugin.com/d/d:bedz55fH
* */

@DelicateCoroutinesApi
class Remote {
    private val url =
        "jdbc:postgresql://bvngbq41lttherknucwg-postgresql.services.clever-cloud.com:50013/bvngbq41lttherknucwg"
    private val user = "ut5btkq9fydn1ibguapa"
    private val password = "C0tqu6aucgOGLtup2zoIP3BmNiNk80"


    //base query execution function
    private suspend fun executeQuery(query: String): ResultSet? = withContext(Dispatchers.Unconfined){
        var result: ResultSet? = null
                //trying establishing connection=
        withContext(Dispatchers.IO) {
            runBlocking {
                Log.i(TAG, "ESTABLISHING CONNECTION")
                try {
                    val conn = DriverManager.getConnection(url, user, password)

                    Log.i(TAG, "Connection Established")
                    //trying executing the query
                    Log.i(TAG, "Created Statement")
                    result = conn.createStatement().executeQuery(query)
                    //closing connection
                    conn?.close()
                } catch (e: Exception) {
                    Log.i(TAG, "Connection Problem: ${e.message}")
                }

            }
        }

        return@withContext result
    }

    //Data Retrieval first
    suspend fun getProducts(productsIDs: List<Int>, filter: Filter):MutableList<Product> {
        val fnTAG = "getProducts Fn:"
        val products = mutableListOf<Product>()
        val filterQuery : String = filterQueryBuilder(filter)
        val query  = """
           SELECT * 
           FROM $products_table
           WHERE $productID_column_name IN ( ${productsIDs.joinToString(", ")} )
           $filterQuery
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                products.add(Product(
                    result.getInt(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getDouble(4),
                    result.getInt(5),
                    result.getInt(6),
                    result.getInt(7),
                    result.getString(8)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return products
    }
    suspend fun getProductsBySubCategoryID(subCategoryID:Int):MutableList<Int> {
        val fnTAG = "getProductsBySubCategoryID Fn:"
        val ids = mutableListOf<Int>()
        val query  = """
           SELECT $productID_column_name
           FROM $products_table
           WHERE $subCategoriesID_column_name = $subCategoryID;
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                ids.add(result.getInt(1))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return ids
    }

    private fun filterQueryBuilder(filter: Filter): String {
        var query  = ""

        if (filter.brand != null) {
            query += "AND\n"
            query += "$brandID_column_name IN ( ${filter.brand.joinToString(", ","'","'")} )"
        }
        if (filter.priceRange != null) {
            query += "AND\n"
            query += "$price_column_name BETWEEN ${filter.priceRange.minPriceRange} AND ${filter.priceRange.maxPriceRange}\n"
        }
        if (filter.verbalSort != null) {
            query += "ORDER BY $productName_column_name ${if (filter.verbalSort == VerbalSortingType.Ascending) "ASC" else "DESC"}\n"
        }
        query+= ";"
        return query
    }


    suspend fun getFavorites(currUser: Int):MutableList<FavoriteItem> {
        val fnTAG = "Favorite Fn:"
        val favorites = mutableListOf<FavoriteItem>()
        val query  = """
           SELECT * 
           FROM $favourite_table
           WHERE $userID_column_name = $currUser
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                favorites.add(FavoriteItem(result.getInt(1)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return favorites
    }


    suspend fun getDeals(dealType:DealsType):MutableList<DealsItem> {
        val fnTAG = "Deals Fn:"
        val deals = mutableListOf<DealsItem>()
        val tableName = if(dealType == DealsType.Daily) dailyDeals_table else storeDeals_table

        val query = """
           SELECT * 
           FROM $tableName
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                deals.add(DealsItem(result.getInt(1),result.getDouble(2)))
        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return deals
    }

    suspend fun getBrands():MutableList<Brand> {
        val fnTAG = "Brands Fn:"
        val brands = mutableListOf<Brand>()
        val query  = """
           SELECT * 
           FROM $brands_table
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                brands.add(Brand(result.getInt(1), result.getString(2),result.getString(3)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return brands
    }

    suspend fun getCategories():MutableList<Category> {
        val fnTAG = "Categories Fn:"
        val categories = mutableListOf<Category>()
        val query  = """
           SELECT * 
           FROM $categories_table
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                categories.add(Category(result.getInt(1), result.getString(2)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        Log.i("LOL", "Categories Retrived ${categories.size}")
        return categories
    }

    suspend fun getSubCategories():MutableList<SubCategory> {
        val fnTAG = "SubCategories Fn:"
        val subCategories = mutableListOf<SubCategory>()
        val query  = """
           SELECT * 
           FROM $subCategories_table
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                subCategories.add(SubCategory(result.getInt(1), result.getInt(2), result.getString(3)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        Log.i("LOL", "SubCategories Retrived ${subCategories.size}")
        return subCategories
    }

    suspend fun getOrders(currUser:Int) :MutableList<Order> {
        val fnTAG = "Orders Fn:"
        val orders = mutableListOf<Order>()
        val query  = """
           SELECT * 
           FROM $orders_table
           WHERE $userID_column_name = $currUser
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                orders.add(Order(result.getInt(1), result.getString(3),result.getDouble(4),result.getString(5),result.getTimestamp(6)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return orders
    }


    suspend fun getOrderItems(orderID:Int) :MutableList<OrderItem> {
        val fnTAG = "OrderItems Fn:"
        val orderItems = mutableListOf<OrderItem>()
        val query  = """
           SELECT * 
           FROM $orderItem_table
           WHERE $orderID_column_name = $orderID
        """
        val result = executeQuery(query)

        //handling incoming resultSet
        try {
            while (result!!.next())
                orderItems.add(OrderItem(result.getInt(1), result.getInt(2), result.getDouble(3),result.getDouble(4),result.getInt(5)))

        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        return orderItems
    }

    suspend fun searchTexts(keyWord: String): MutableList<String> {
        val fnTAG = "SearchTexts fn"
        val limit = 30
        val searchTexts = mutableListOf<String>()

        val query = """
            SELECT $productName_column_name AS result FROM $products_table WHERE Name ILIKE '%$keyWord%'
            UNION
            SELECT $brandName_column_name AS result FROM $brands_table WHERE Name ILIKE '%$keyWord%'
            UNION
            SELECT $categoryName_column_name AS result FROM $categories_table WHERE category ILIKE '%$keyWord%'
            UNION
            SELECT $subCategoriesName_column_name AS result FROM $subCategories_table WHERE name ILIKE '%$keyWord%'
            LIMIT $limit;
        """

        val result = executeQuery(query)
        //handling incoming resultSet
        try {
            while (result!!.next())
                searchTexts.add(result.getString(1))
        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        Log.i("LOL", "From search text retrieved ${searchTexts.size}, from keyword $keyWord")
        return  searchTexts
    }

    suspend fun searchProducts(keyWord: String): ArrayList<Int>{
        val fnTAG = "SearchTexts fn"
        val ids = mutableListOf<Int>()

        val query = """
            SELECT $productID_column_name AS result FROM $products_table WHERE $productName_column_name ILIKE '%$keyWord%'
            UNION
            SELECT P.$productID_column_name AS result FROM $products_table P
            JOIN $brands_table B ON P.$brandID_column_name = B.$brandID_column_name
            WHERE B.$brandName_column_name ILIKE '%$keyWord%'
            UNION
            SELECT P.$productID_column_name AS result FROM $products_table P
            JOIN $subCategories_table SC ON P.$subCategoriesID_column_name = SC.$subCategoriesID_column_name
            JOIN $categories_table C ON SC.$categoriesID_column_name = C.$categoriesID_column_name
            WHERE C.$categoryName_column_name ILIKE '%$keyWord%'
            UNION
            SELECT P.$productID_column_name AS result FROM $products_table P
            JOIN $subCategories_table SC ON P.$subCategoriesID_column_name = SC.$subCategoriesID_column_name
            WHERE SC.$subCategoriesName_column_name ILIKE '%$keyWord%';
        """
        val result = executeQuery(query)
        //handling incoming resultSet
        try {
            while (result!!.next())
                ids.add(result.getInt(1))
        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
        }
        Log.i("LOL", "From Products retrieved ${ids.size}, from keyword $keyWord")
        return java.util.ArrayList(ids)
    }

    //Functions
    //base fn
    private suspend fun functionCall(query: String, fnTAG:String):Int{

        Log.i("LOL","fn call called")
        var response = -1
        //handling incoming resultSet
            val result = executeQuery(query)
        try {
            if (result!!.next())
                response = result.getInt(1)
            Log.i(TAG, "Closing Connection || Result: $response")
            result.close()
        }catch (e:Exception) {
            Log.i(TAG, "$fnTAG $e")
            Log.i(TAG, "$fnTAG $result")
        }
        return response
    }

    //Order Fns
    suspend fun placeOrder(userID:Int, order:Order = Order()):Int{
        //* Place order ID = null when calling the function

        //response -> OrderID OK
        //response -> -1 VoucherINVALID
        //response -> -2 ERROR
        //response -> -3 NoResponse

        val fnTAG = "PlaceOrder Fn:"
        val query  = """
           SELECT $placeOrder_function($userID, '', ${order.totalPrice}, '${order.address}');
        """
        return functionCall(query, fnTAG)
    }

    //Modified to take more than on item at once
    suspend fun placeOrderItems(orderID:Int, orderItems: MutableList<OrderItem>):Int{
        //response -> No Response
        val fnTAG = "PlaceOrderItems Fn:"
        var items = ""
        var countQuery = ""
        orderItems.forEach {orderItem-> items += "(${orderID}, ${orderItem.productID}, ${orderItem.price}, ${orderItem.discount}, ${orderItem.quantity}),\n" ;countQuery+="SELECT increment_purchase_count(${orderItem.productID})\n;"}
        val query  = """
           INSERT INTO $orderItem_table ($orderID_column_name, $productID_column_name, $unitPrice_column_name, $discount_column_name, $quantity_column_name)
           VALUES 
           ${items.dropLast(2)};
           $countQuery
        """
        Log.i("Lol", "Order ITem placing queries $query with orderItems size: ${orderItems.size}")
        return functionCall(query, fnTAG)
    }


    //Auth Fns
    suspend fun getUserID(email:String):Int{
        //response -> UserID OK
        //response -> -1 ERROR
        Log.i("LOL","Get User called")
        val fnTAG = "getUserID Fn:"
        val query  = """
           SELECT $getUserID_function('$email');
        """
        return functionCall(query, fnTAG)
    }

    suspend fun registerUser(user: User):Int{
        //response -> 200 OK
        //response -> 501 INVALID EMAIL
        //response -> 400 ERROR
        //response -> -1 LOCAL_ERROR

        val fnTAG = "registerUser Fn:"
        val query  = """
           SELECT $registerUser_function('${user.email}', '${user.password}', '${user.firstName}', '${user.lastName}', '${user.phoneNumber}');
        """
        return functionCall(query, fnTAG)
    }

    suspend fun authentication(user: User):Int{
        //response -> 200 OK
        //response -> 501 INVALID EMAIL
        //response -> 401 INVALID PASSWORD
        //response -> -1 LOCAL_ERROR

        val fnTAG = "authentication Fn:"
        val query  = """
           SELECT $authentication_function('${user.email}', '${user.password}');
        """

        return functionCall(query, fnTAG)
    }

    suspend fun registerAddress(userID: Int, address: Address):Int{
        //response -> 200 OK
        //response -> 400 ERROR
        //response -> -1 LOCAL_ERROR

        val fnTAG = "registerAddress Fn:"
        val query  = """
           SELECT $registerAddress_function($userID ,'${address.name}', ${address.primary});
        """
        return functionCall(query, fnTAG)
    }

    //other helping functions
    suspend fun checkVoucher(voucher:String):Int{
        //response -> 200 OK
        //response -> 501 ALREADY USED
        //response -> 401 INVALID VOUCHER
        //response -> -1 LOCAL_ERROR

        val fnTAG = "checkVoucher Fn:"
        val query  = """
           SELECT $voucher_function('$voucher');
        """
        return functionCall(query, fnTAG)
    }

    suspend fun addFavorite(userID: Int, productID:Int):Int{
        //response -> 200 OK
        //response -> 400 ERROR
        //response -> -1 LOCAL_ERROR

        val fnTAG = "addFavorite Fn:"
        val query  = """
           SELECT $addFavorite_function($userID, $productID);
        """
        return functionCall(query, fnTAG)
    }

    suspend fun removeFavorite(userID: Int, productID:Int):Int{
        //response -> 200 OK
        //response -> 400 ERROR
        //response -> -1 LOCAL_ERROR

        val fnTAG = "removeFavorite Fn:"
        val query  = """
           SELECT $removeFavorite_function($userID, $productID);
        """
        return functionCall(query, fnTAG)
    }

    suspend fun checkStock(productID:Int, requiredQuantity:Int):Int{
        //response -> 200 OK
        //response -> 400 ERROR
        //response -> -1 LOCAL_ERROR

        val fnTAG = "checkStock Fn:"
        val query  = """
           SELECT $checkStock_function($productID, $requiredQuantity);
        """
        return functionCall(query, fnTAG)
    }

    companion object {
        const val TAG = "REMOTE CLASS ERROR"
        //TABLES NAMES
        const val brands_table = "Brands"
        const val favourite_table = "Favourite"
        const val products_table = "Products"
        const val dailyDeals_table = "Daily_deals"
        const val storeDeals_table = "Store_deals"
        const val categories_table = "Categories"
        const val subCategories_table = "SubCategories"
        const val orders_table = "Orders"
        const val orderItem_table = "Order_item"

        //Columns
        const val userID_column_name = "user_id"
        const val orderID_column_name = "order_id"
        const val productID_column_name = "product_id"
        const val productName_column_name = "name"
        const val unitPrice_column_name = "unit_price"
        const val discount_column_name = "discount"
        const val quantity_column_name = "quantity"
        const val purchase_count_column_name = "purchase_count"
        const val brandID_column_name = "brandID"
        const val brandName_column_name = "name"
        const val price_column_name = "price"
        const val categoryName_column_name = "category"
        const val subCategoriesName_column_name = "name"
        const val subCategoriesID_column_name = "subcategory_id"
        const val categoriesID_column_name = "category_id"


        //Functions
        const val registerUser_function = "RegisterUser"
        const val registerAddress_function = "RegisterAddress"
        const val getUserID_function = "GetUserID"
        const val authentication_function = "Authentication"
        const val checkStock_function = "CheckStock"
        const val placeOrderItem_function = "PlaceOrderItem"
        const val placeOrder_function = "PlaceOrder"
        const val addFavorite_function = "AddFavorite"
        const val removeFavorite_function = "RemoveFavorite"
        const val voucher_function = "CheckVoucher"

    }
}