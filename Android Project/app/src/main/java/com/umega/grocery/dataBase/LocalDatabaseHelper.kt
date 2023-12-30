package com.umega.grocery.dataBase
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.umega.grocery.utill.Brand
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.DealsItem
import com.umega.grocery.utill.DealsItemLocal
import com.umega.grocery.utill.FavoriteItem
import com.umega.grocery.utill.FavouriteItemLocal
import com.umega.grocery.utill.Filter
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import com.umega.grocery.utill.ResultItem
import com.umega.grocery.utill.SubCategory

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "GroceryShopping"
        const val DATABASE_VERSION = 2
        //define table names
        const val cartItems_table = "Cart_Items"
        const val products_table = "Products"
        const val brands_table = "Brands"
        const val favourite_table = "Favourite"
        const val dailyDeals_table = "Daily_Deals"
        const val categories_table = "Categories"
        const val subCategories_table = "Sub_Categories"
        const val storeDeals_table = "Store_Deals"
        const val order_table = "Orders"
        const val orderItem_table = "Order_Items"
        const val addresses_table = "Addresses"
        //define user_table items
        const val  user_table_userID ="UserID"
        const val user_table_username  ="Username"
        const val  user_table_password ="Password"
        const val  user_table_email ="Email"
        //define cartItems_table
        const val  cartItems_table_itemID ="ItemID"
        const val  cartItems_table_productID ="ProductID"
        const val  cartItems_table_quantity ="Quantity"
        //define Products_table
        const val  Products_table_productID ="ProductID"
        const val Products_table_name  ="Name"
        const val  Products_table_price ="Price"
        const val  Products_table_brandId = "BrandID"
        const val  Products_table_stockQuantity ="StockQuantity"
        const val Products_table_imgName ="imgName"
        const val Products_table_subcategoryId = "SubCategoriesId"
        const val Products_table_purchaseCount="purchaseCount"
        //define Brands_table
        const val  Brands_table_brandID ="BrandID"
        const val Brands_table_name  ="Name"
        const val  Brands_table_nationality ="Nationality"
        //define Favourite_table
        const val  Favourite_table_productID ="ProductID"
        //define dailyDeals_table
        const val  dailyDeals_table_productID ="ProductID"
        const val  dailyDeals_table_discount ="Discount"
        //define storeDeals_table
        const val  storeDeals_table_productID ="ProductID"
        const val  storeDeals_table_discount ="Discount"
        //define categories_table
        const val  categories_table_categoryID ="CategoryID"
        const val  categories_table_categoryName ="CategoryName"
        //define subCategories_table
        const val  subCategories_table_subCategoryID ="SubCategoryID"
        const val  subCategories_table_categoryID ="CategoryID"
        const val  subCategories_table_subCategoryName ="SubCategoryName"
        //define Orders table
        const val order_table_orderId = "OrderId"
        const val order_table_voucher = "Voucher"
        const val order_table_date = "Date"
        const val order_table_address = "Address"
        const val order_table_totalPrice = "TotalPrice"
        // define Order_Items table
        const val orderItem_table_ID = "Id"
        const val orderItem_table_orderID = "OrderId"
        const val orderItem_table_productID = "ProductID"
        const val orderItem_table_quantity = "Quantity"
        const val orderItem_table_discount = "Discount"
        const val orderItem_table_fullPrice = "FullPrice"
        //define Addresses table
        const val addresses_table_address = "Address"
        const val addresses_table_primary = "Primary"
    }
    //create tables
    private val createTableProducts = """
    CREATE TABLE IF NOT EXISTS $products_table (
        $Products_table_productID INTEGER PRIMARY KEY ,
        $Products_table_name TEXT NOT NULL,
        $Products_table_price REAL NOT NULL,
        $Products_table_stockQuantity INTEGER NOT NULL,
        $Products_table_brandId INTEGER,
        $Products_table_imgName TEXT,
        $Products_table_purchaseCount INTEGER, 
        $Products_table_subcategoryId INTEGER
    );
"""
    private val createTableCartItems = """
            CREATE TABLE IF NOT EXISTS $cartItems_table (
                $cartItems_table_itemID INTEGER PRIMARY KEY AUTOINCREMENT,
                $cartItems_table_productID INTEGER NOT NULL,
                $cartItems_table_quantity INTEGER NOT NULL,
                FOREIGN KEY($cartItems_table_productID) REFERENCES $products_table($Products_table_productID)
            );
        """
    private val createTableBrands = """
            CREATE TABLE IF NOT EXISTS $brands_table (
                $Brands_table_brandID INTEGER PRIMARY KEY AUTOINCREMENT,
                $Brands_table_name TEXT NOT NULL,
                $Brands_table_nationality TEXT
            );
        """
    private val createTableFavourite = """
            CREATE TABLE IF NOT EXISTS $favourite_table (
                $Favourite_table_productID INTEGER PRIMARY KEY,
                FOREIGN KEY($Favourite_table_productID) REFERENCES $products_table($Products_table_productID)
            );
        """
    private val createTableDailyDeals = """
            CREATE TABLE IF NOT EXISTS $dailyDeals_table (
                $dailyDeals_table_productID INTEGER PRIMARY KEY ,
                $dailyDeals_table_discount REAL,
                FOREIGN KEY($dailyDeals_table_productID) REFERENCES $products_table($Products_table_productID)
            );
        """
    private val createTableStoreDeals = """
    CREATE TABLE IF NOT EXISTS $storeDeals_table (
        $storeDeals_table_productID INTEGER PRIMARY KEY,
        $storeDeals_table_discount REAL,
        FOREIGN KEY ($storeDeals_table_productID) REFERENCES $products_table($Products_table_productID)
    );
    """
    private val createTableCategories = """
            CREATE TABLE IF NOT EXISTS $categories_table (
                $categories_table_categoryID INTEGER PRIMARY KEY,
                $categories_table_categoryName TEXT
            );
        """
    private val createTableSubCategories = """
            CREATE TABLE IF NOT EXISTS $subCategories_table (
                $subCategories_table_subCategoryID INTEGER PRIMARY KEY,
                $subCategories_table_categoryID INTEGER NOT NULL,
                $subCategories_table_subCategoryName TEXT,
                FOREIGN KEY($subCategories_table_categoryID) REFERENCES $categories_table($categories_table_categoryID)
            );
        """
    private val createTableOrder = """
    CREATE TABLE IF NOT EXISTS $order_table (
        $order_table_orderId INTEGER PRIMARY KEY,
        $order_table_voucher TEXT,
        $order_table_date DATETIME,
        $order_table_address TEXT,
        $order_table_totalPrice REAL
    );
    """
    private val createTableOrderItem = """
    CREATE TABLE IF NOT EXISTS $orderItem_table (
        $orderItem_table_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $orderItem_table_orderID INTEGER,
        $orderItem_table_productID INTEGER,
        $orderItem_table_quantity INTEGER,
        $orderItem_table_discount INTEGER,
        $orderItem_table_fullPrice REAL,
        FOREIGN KEY ($orderItem_table_orderID) REFERENCES $order_table($order_table_orderId),
        FOREIGN KEY ($orderItem_table_productID) REFERENCES $products_table($Products_table_productID)
    );
    """
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableCategories)
        db?.execSQL(createTableSubCategories)
        db?.execSQL(createTableBrands)
        db?.execSQL(createTableProducts)
        db?.execSQL(createTableCartItems)
        db?.execSQL(createTableDailyDeals)
        db?.execSQL(createTableStoreDeals)
        db?.execSQL(createTableFavourite)
        db?.execSQL(createTableOrder)
        db?.execSQL(createTableOrderItem)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
    // DailyDealsTable queries
    fun getDailyStoreProductIds(): List<Int> {
        val resultList = mutableListOf<Int>()

        val query = """
        SELECT $dailyDeals_table_productID AS productID FROM $dailyDeals_table
        UNION
        SELECT $storeDeals_table_productID AS productID FROM $storeDeals_table
    """

        readableDatabase.use { db ->
            db.rawQuery(query, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val productId = cursor.getInt(cursor.getColumnIndexOrThrow("productID"))
                    resultList.add(productId)
                }
            }
        }
        Log.i("lolids",resultList.toString())
        return resultList
    }
    fun getAllDailyDeals(): List<DealsItemLocal> {
        val dailyDealsList = mutableListOf<DealsItemLocal>()
        val query = """
       SELECT P.$Products_table_name, 
              (P.$Products_table_price - DD.$dailyDeals_table_discount) AS productPriceAfterDiscount,
              P.$Products_table_imgName,
              COUNT(F.$Favourite_table_productID) > 0 as isFavourite,
              P.$Products_table_productID
       FROM $dailyDeals_table DD
       INNER JOIN $products_table P ON DD.$dailyDeals_table_productID = P.$Products_table_productID
       LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
       LEFT JOIN $cartItems_table CI ON P.$Products_table_productID = CI.$cartItems_table_productID
       GROUP BY P.$Products_table_productID
       """
        try {
            readableDatabase.use { db ->
                db.rawQuery(query, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val productName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                        val productPriceAfterDiscount =
                            cursor.getDouble(cursor.getColumnIndexOrThrow("productPriceAfterDiscount"))
                        val imgName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))
                        val isFavourite =
                            cursor.getInt(cursor.getColumnIndexOrThrow("isFavourite")) == 1
                        val productId =
                            cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                        val dealsItemLocal = DealsItemLocal(
                            productName = productName,
                            productPriceAfterDiscount = productPriceAfterDiscount,
                            imgName = imgName,
                            isFavourite = isFavourite,
                            productId = productId
                        )
                        dailyDealsList.add(dealsItemLocal)
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("lolerror", e.toString())
        } finally {
            // Close the database to release resources
            readableDatabase.close()
        }
        return dailyDealsList
    }
    fun insertDailyDeals(dailyDeals: List<DealsItem>) {
        writableDatabase.beginTransaction()
        try {
            for ((productID, discount) in dailyDeals) {
                val values = ContentValues().apply {
                    put(dailyDeals_table_productID, productID)
                    put(dailyDeals_table_discount, discount)
                }

                writableDatabase.insertWithOnConflict(
                    dailyDeals_table,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }
    //StoreDealsTable queries
    fun getAllStoreDeals(): List<DealsItemLocal> {
        val storeDealsList = mutableListOf<DealsItemLocal>()
        val query = """
       SELECT P.$Products_table_name, 
              (P.$Products_table_price - SD.$storeDeals_table_discount) AS productPriceAfterDiscount,
              P.$Products_table_imgName,
              COUNT(F.$Favourite_table_productID) > 0 as isFavourite,
              P.$Products_table_productID
       FROM $storeDeals_table SD
       INNER JOIN $products_table P ON SD.$storeDeals_table_productID = P.$Products_table_productID
       LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
       LEFT JOIN $cartItems_table CI ON P.$Products_table_productID = CI.$cartItems_table_productID
       GROUP BY P.$Products_table_productID
    """
        try {
            readableDatabase.use { db ->
                db.rawQuery(query, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val productName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                        val productPriceAfterDiscount =
                            cursor.getDouble(cursor.getColumnIndexOrThrow("productPriceAfterDiscount"))
                        val imgName =
                            cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))
                        val isFavourite =
                            cursor.getInt(cursor.getColumnIndexOrThrow("isFavourite")) == 1
                        val productId =
                            cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                        val dealsItemLocal = DealsItemLocal(
                            productName = productName,
                            productPriceAfterDiscount = productPriceAfterDiscount,
                            imgName = imgName,
                            isFavourite = isFavourite,
                            productId = productId
                        )
                        storeDealsList.add(dealsItemLocal)
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("lolerror", e.toString())
        } finally {
            // Close the database to release resources
            readableDatabase.close()
        }
        Log.i("lol store list", storeDealsList.toString())
        return storeDealsList
    }
    fun insertStoreDeals(storeDeals: List<DealsItem>) {
        Log.i("loliteminsertstore", storeDeals.toString())
        writableDatabase.beginTransaction()
        try {
            for ((productID, discount) in storeDeals) {
                val values = ContentValues().apply {
                    put(storeDeals_table_productID, productID)
                    put(storeDeals_table_discount, discount)
                }

                writableDatabase.insertWithOnConflict(
                    storeDeals_table,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }
    // cartItems table queries
    fun getAllCartItems(): List<CartItem> {
        val cartItemsList = mutableListOf<CartItem>()
        val query = """
       SELECT P.$Products_table_name,
              P.$Products_table_price,
              CI.$cartItems_table_quantity,
              P.$Products_table_price * CI.$cartItems_table_quantity AS totalPrice,
              P.$Products_table_productID,
              P.$Products_table_imgName
       FROM $cartItems_table CI
       INNER JOIN $products_table P ON CI.$cartItems_table_productID = P.$Products_table_productID
    """
        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val itemName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(cartItems_table_quantity))
                    val totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice"))
                    val productId = cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                    val imgName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))

                    val cartItem = CartItem(
                        itemName = itemName,
                        price = price,
                        itemQuantity = productQuantity,
                        totalPrice = totalPrice,
                        productId = productId,
                        imgName = imgName
                    )

                    cartItemsList.add(cartItem)
                }
            }
        }
        return cartItemsList
    }
    fun insertCartItem(productID: Int, quantity: Int) {
        writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put(cartItems_table_productID, productID)
                put(cartItems_table_quantity, quantity)
            }
            db.insert(cartItems_table, null, contentValues)
        }
    }
    fun updateCartItemQuantity(productID: Int, newQuantity: Int) {
        writableDatabase.use { db ->
            val contentValues = ContentValues().apply {
                put(cartItems_table_quantity, newQuantity)
            }
            val whereClause = "$cartItems_table_productID = ?"
            val whereArgs = arrayOf(productID.toString())
            db.update(cartItems_table, contentValues, whereClause, whereArgs)
        }
    }
    fun deleteCartItem(productID: Int) {
        writableDatabase.use { db ->
            val whereClause = "$cartItems_table_productID = ?"
            val whereArgs = arrayOf(productID.toString())
            db.delete(cartItems_table, whereClause, whereArgs)
        }
    }
    // categories and subCategories table queries
    fun getAllSubcategoriesByCategory(categoryId: Int): List<SubCategory> {
        val subcategoriesList = mutableListOf<SubCategory>()
        val query = """
        SELECT $subCategories_table_subCategoryID, $subCategories_table_categoryID, $subCategories_table_subCategoryName
        FROM $subCategories_table
        WHERE $subCategories_table_categoryID = ?
    """
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(categoryId.toString())).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(subCategories_table_subCategoryID))
                    val categoryID = cursor.getInt(cursor.getColumnIndexOrThrow(subCategories_table_categoryID))
                    val subcategoryName = cursor.getString(cursor.getColumnIndexOrThrow(subCategories_table_subCategoryName))

                    val subCategory = SubCategory(id, categoryID, subcategoryName)
                    subcategoriesList.add(subCategory)
                }
            }
        }
        Log.i("lolsubcategori",subcategoriesList.toString())
        return subcategoriesList
    }

    fun getAllCategories(): List<Category> {
        val categoriesList = mutableListOf<Category>()
        val query = """
        SELECT $categories_table_categoryID, $categories_table_categoryName
        FROM $categories_table
        """
        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(categories_table_categoryID))
                    val categoryName = cursor.getString(cursor.getColumnIndexOrThrow(categories_table_categoryName))

                    val category = Category(id, categoryName)
                    categoriesList.add(category)
                }
            }
        }
        return categoriesList
    }
    fun insertCategories(categories: List<Category>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (category in categories) {
                    val values = ContentValues().apply {
                        put(categories_table_categoryID, category.id)
                        put(categories_table_categoryName, category.name)
                    }
                    db.insert(categories_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun insertSubCategories(subCategories: List<SubCategory>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (subCategory in subCategories) {
                    val values = ContentValues().apply {
                        put(subCategories_table_subCategoryID, subCategory.id)
                        put(subCategories_table_categoryID, subCategory.categoryID)
                        put(subCategories_table_subCategoryName, subCategory.name)
                    }
                    db.insert(subCategories_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun getProductsBySubcategoryId(subcategoryId: Int): List<ResultItem> {
        val productList = mutableListOf<ResultItem>()
        val query = """
        SELECT P.$Products_table_name, 
               P.$Products_table_price,
               P.$Products_table_imgName,
               COUNT(F.$Favourite_table_productID) > 0 as isFavourite,
               P.$Products_table_productID
        FROM $products_table P
        LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
        WHERE P.$Products_table_subcategoryId = ?
        """
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(subcategoryId.toString())).use { cursor ->
                while (cursor.moveToNext()) {
                    val productName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val imgName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))
                    val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavourite")) == 1
                    val productId = cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))

                    val resultItem = ResultItem(
                        productName = productName,
                        price = productPrice,
                        imgName = imgName,
                        isFavourite = isFavourite,
                        productId = productId
                    )
                    productList.add(resultItem)
                }
            }
        }
        return productList
    }

    fun getProducts(productIDs: MutableList<Int>, filter: Filter) :Pair<MutableList<Product>,MutableList<Int>> {
        val productList = mutableListOf<Product>()
        val missingProducts = mutableListOf<Int>()
        for (x in productIDs){
            val product = getProduct(x, filter)
            if (product != null)
                productList.add(product)
            else
                missingProducts.add(x)
        }
        return Pair(productList,missingProducts)
    }

    //TODO handle the filter in a WHERE statement
    // note that if inner values of filter == null for each one of them it means that no filter applied
    // for that specific value
    //  look at the get products in the remote class for more specifications
    fun getProduct(productID:Int, filter: Filter = Filter()):Product? {
        val selectProductQuery = """
        SELECT *
        FROM $products_table
        WHERE $Products_table_productID = ?
    """
        //DONE
        return try {
            readableDatabase.use { db ->
                val cursor = db.rawQuery(selectProductQuery, arrayOf(productID.toString()))
                if (cursor.moveToFirst()) {
                    val product = Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_brandId)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_stockQuantity)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_subcategoryId)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_purchaseCount)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))
                    )
                    cursor.close()
                    product
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
            null
        }
    }


    //SEARCH fun will be implemented once reached the task

    /*
    fun searchProducts(token: String): List<ResultItem> {
        val productList = mutableListOf<ResultItem>()
        val query = """
        SELECT P.$Products_table_name,
               P.$Products_table_price,
               P.$Products_table_quantity,
               P.$Products_table_productID,
               COUNT(F.$Favourite_table_productID) > 0 as isFavorite,
               brands.$Brands_table_name as brandName
        FROM $products_table P
        LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
        LEFT JOIN $brands_table brands ON P.$Products_table_brandName = brands.$Brands_table_name
        WHERE P.$Products_table_name LIKE ? OR brands.$Brands_table_name LIKE ?
        GROUP BY P.$Products_table_productID
    """
        val searchTerm = "%$token%" // Add % to search for partial matches
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(searchTerm, searchTerm)).use { cursor ->
                while (cursor.moveToNext()) {
                    val productName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_quantity))
                    val productId = cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                    val isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1
                    val brandName = cursor.getString(cursor.getColumnIndexOrThrow("brandName"))

                    val resultItem = ResultItem(productName, price, productQuantity, productId, isFavorite, brandName)
                    productList.add(resultItem)
                }
            }
        }
        return productList
    }
    fun searchProductsResultBar(token: String): List<String> {
        val resultList = mutableListOf<String>()
        // Search in product name
        val productQuery = """
        SELECT $Products_table_name
        FROM $products_table
        WHERE $Products_table_name LIKE ?
    """
        // Search in brand name
        val brandQuery = """
        SELECT brands.$Brands_table_name
        FROM $products_table P
        LEFT JOIN $brands_table brands ON P.$Products_table_brandName = brands.$Brands_table_name
        WHERE brands.$Brands_table_name LIKE ?
    """
        val searchTerm = "%$token%" // Add % to search for partial matches
        readableDatabase.use { db ->
            db.rawQuery(productQuery, arrayOf(searchTerm)).use { cursor ->
                while (cursor.moveToNext()) {
                    val productName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    resultList.add(productName)
                }
            }
            db.rawQuery(brandQuery, arrayOf(searchTerm)).use { cursor ->
                while (cursor.moveToNext()) {
                    val brandName = cursor.getString(cursor.getColumnIndexOrThrow(Brands_table_name))
                    resultList.add(brandName)
                }
            }
        }
        return resultList
    }*/
    // products table
    fun insertProducts(products: List<Product>) {
        try {
            writableDatabase.use { db ->
                db.beginTransaction()
                try {
                    for (product in products) {
                        val values = ContentValues().apply {
                            put(storeDeals_table_productID,product.id)
                            put(Products_table_name, product.name)
                            put(Products_table_price, product.price)
                            put(Products_table_stockQuantity, product.stockQuantity)
                            put(Products_table_brandId, product.brandID)
                            put(Products_table_imgName, product.imgName)
                            put(Products_table_purchaseCount, product.purchaseCount)
                            put(Products_table_subcategoryId, product.subCategoryID)
                        }
                        db.insertWithOnConflict(
                            products_table,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE
                        )
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
        } catch (e: Exception) {
            Log.e("loldatabase", "Error inserting products: ${e.message}")
        }
    }
    // favourite table
    fun insertFavoriteProducts(favoriteItems: List<FavoriteItem>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (favoriteItem in favoriteItems) {
                    val values = ContentValues().apply {
                        put(Favourite_table_productID, favoriteItem.productID)
                    }
                    db.insert(favourite_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun insertFavoriteProduct(favoriteItem: FavouriteItemLocal?) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                val values = ContentValues().apply {
                    put(Favourite_table_productID, favoriteItem?.productID)
                }
                db.insert(favourite_table, null, values)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun deleteFavoriteProduct(favoriteItem: FavouriteItemLocal?) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                // Define the WHERE clause to identify the row to delete
                val whereClause = "$Favourite_table_productID = ?"
                val whereArgs = arrayOf(favoriteItem?.productID.toString())

                // Perform the deletion
                db.delete(favourite_table, whereClause, whereArgs)

                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun getAllFavoriteProducts(): List<FavouriteItemLocal> {
        val favoriteItemList = mutableListOf<FavouriteItemLocal>()
        val query = """
        SELECT P.$Products_table_productID,
               P.$Products_table_name,
               P.$Products_table_price,
               P.$Products_table_imgName
        FROM $favourite_table F
        INNER JOIN $products_table P ON F.$Favourite_table_productID = P.$Products_table_productID
        """
        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val productID = cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                    val productName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val imgName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_imgName))
                    val favouriteItemLocal = FavouriteItemLocal(
                        productID = productID,
                        name = productName,
                        price = productPrice,
                        imgName = imgName
                    )
                    favoriteItemList.add(favouriteItemLocal)
                }
            }
        }
        return favoriteItemList
    }
    // brands table
    //Done add nationality to the table
    fun insertBrands(brands: List<Brand>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (brand in brands) {
                    val values = ContentValues().apply {
                        put(Brands_table_name, brand.name)
                        put(Brands_table_brandID, brand.id)
                        put(Brands_table_nationality, brand.nationality)
                    }
                    db.insert(brands_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun getBrandById(brandId: Int): Brand? {
        val query = "SELECT * FROM $brands_table WHERE $Brands_table_brandID = ?"
        var brand: Brand? = null
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(brandId.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    val brandID = cursor.getInt(cursor.getColumnIndexOrThrow(Brands_table_brandID))
                    val brandName = cursor.getString(cursor.getColumnIndexOrThrow(Brands_table_name))
                    val brandNationality = cursor.getString(cursor.getColumnIndexOrThrow(Brands_table_nationality))
                    brand = Brand(
                        id = brandID,
                        name = brandName,
                        nationality = brandNationality
                    )
                }
            }
        }
        return brand
    }
    //order table
    fun insertOrders(orders: List<Order>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (order in orders) {
                    val values = ContentValues().apply {
                        put(order_table_voucher, order.voucher)
                        put(order_table_totalPrice, order.totalPrice)
                        put(order_table_address, order.address)
                        put(order_table_date, order.date.toString()) // Assuming date is a String
                    }
                    val orderId = db.insert(order_table, null, values)
                    order.id = orderId.toInt()
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    fun insertOrder(order: Order) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                val values = ContentValues().apply {
                    put(order_table_voucher, order.voucher)
                    put(order_table_totalPrice, order.totalPrice)
                    put(order_table_address, order.address)
                    put(order_table_date, order.date.toString()) // Assuming date is a String
                }
                val orderId = db.insert(order_table, null, values)
                order.id = orderId.toInt()
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    // TODO function to get back orders
    // order items table
    fun insertOrderItems(orderItems: List<OrderItem>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                for (orderItem in orderItems) {
                    val values = ContentValues().apply {
                        put(orderItem_table_orderID, orderItem.orderID)
                        put(orderItem_table_productID, orderItem.productID)
                        put(orderItem_table_quantity, orderItem.quantity)
                        put(orderItem_table_discount, orderItem.discount)
                        put(orderItem_table_fullPrice, orderItem.price * (1 - orderItem.discount / 100))
                    }
                    db.insert(orderItem_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    //TODO Adress table
    //clear functions
    fun clearCategoriesAndSubCategories() {
        writableDatabase.use { db ->
            db.execSQL("DELETE FROM $categories_table")
            db.execSQL("DELETE FROM $subCategories_table")
        }
    }

    fun clearBrandsTable() {
        writableDatabase.use { db ->
            db.execSQL("DELETE FROM $brands_table")
        }
    }



}