package com.umega.grocery.dataBase
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.umega.grocery.utill.CartItem
import com.umega.grocery.utill.DailyDealsItem
import com.umega.grocery.utill.ResultItem
import com.umega.grocery.utill.SearchItem

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /*TODO
    *  1. Remove BrandName, BrandNationality in products Table, add instead BrandId ForeignKey referenced to PK of Brands Table
    *  2. remove quantity column in product table
    *  3. add subcategoryId fk column in product table referenced to subcategoryID in subcategory Table
    *  4. remove subcategoryID in brands table
    *  5. Favorite table not created below, create the table favorite
    *  6. create StoreDeals table same structure as dailyDeals
    *  7. create Order Table contains columns (orderId "INT", Voucher "String")
    *  8. create orderItem table contains columns (ID "Incremental INT", OrderID "FK to order table", productID "FK to product Table", quantity "INT")*/
    companion object {
        const val DATABASE_NAME = "GroceryShopping"
        const val DATABASE_VERSION = 1
        //define table names
        const val cartItems_table = "Cart_Items"
        const val products_table = "Products"
        const val brands_table = "Brands"
        const val favourite_table = "Favourite"
        const val dailyDeals_table = "Daily_Deals"
        const val categories_table = "Categories"
        const val subCategories_table = "Sub_Categories"
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
        const val  Products_table_brandName ="BrandName"
        const val  Products_table_brandNationality ="BrandNationality"
        const val  Products_table_price ="Price"
        const val  Products_table_stockQuantity ="StockQuantity"
        const val Products_table_quantity ="Quantity"
        //define Brands_table
        const val  Brands_table_brandID ="BrandID"
        const val Brands_table_name  ="Name"
        const val  Brands_table_nationality ="Nationality"
        const val  Brands_table_subCategoriesId ="SubCategoriesId"
        //define Favourite_table
        const val  Favourite_table_productID ="ProductID"
        //define dailyDeals_table
        const val  dailyDeals_table_productID ="ProductID"
        const val  dailyDeals_table_discount ="Discount"
        //define categories_table
        const val  categories_table_categoryID ="CategoryID"
        const val  categories_table_categoryName ="CategoryName"
        //define subCategories_table
        const val  subCategories_table_subCategoryID ="SubCategoryID"
        const val  subCategories_table_categoryID ="CategoryID"
        const val  subCategories_table_subCategoryName ="SubCategoryName"
    }
    //create tables
    private val createTableProducts = """
            CREATE TABLE IF NOT EXISTS $products_table (
                $Products_table_productID INTEGER PRIMARY KEY AUTOINCREMENT,
                $Products_table_name TEXT NOT NULL,
                $Products_table_brandName TEXT,
                $Products_table_brandNationality TEXT,
                $Products_table_price REAL NOT NULL,
                $Products_table_stockQuantity INTEGER NOT NULL,
                $Products_table_quantity TEXT NOT NULL
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
                $Brands_table_nationality TEXT,
                $Brands_table_subCategoriesId INTEGER NOT NULL,
                FOREIGN KEY($Brands_table_subCategoriesId) REFERENCES $subCategories_table($subCategories_table_subCategoryID)
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
                $dailyDeals_table_productID INTEGER PRIMARY KEY,
                $dailyDeals_table_discount REAL,
                FOREIGN KEY($dailyDeals_table_productID) REFERENCES $products_table($Products_table_productID)
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
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableCategories)
        db?.execSQL(createTableSubCategories)
        db?.execSQL(createTableBrands)
        db?.execSQL(createTableProducts)
        db?.execSQL(createTableCartItems)
        db?.execSQL(createTableDailyDeals)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
    // DailyDealsTable queries
    fun getAllDailyDeals(): List<DailyDealsItem> {
        val dailyDealsList = mutableListOf<DailyDealsItem>()
        val query = """
       SELECT P.$Products_table_name, 
              P.$Products_table_price, 
              DD.$dailyDeals_table_discount,
              P.$Products_table_productID,
              COUNT(F.$Favourite_table_productID) > 0 as isFavourite,
              P.$Products_table_quantity
       FROM $dailyDeals_table DD
       INNER JOIN $products_table P ON DD.$dailyDeals_table_productID = P.$Products_table_productID
       LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
       LEFT JOIN $cartItems_table CI ON P.$Products_table_productID = CI.$cartItems_table_productID
       GROUP BY P.$Products_table_productID
    """
        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val productName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val discount = cursor.getDouble(cursor.getColumnIndexOrThrow(dailyDeals_table_discount))
                    val productID = cursor.getInt(cursor.getColumnIndexOrThrow(Products_table_productID))
                    val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavourite")) == 1
                    val quantity = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_quantity))
                    // Calculate discounted price
                    val discountedPrice = productPrice * discount
                    // Create DailyDealsItem and add to the list
                    val dailyDealsItem = DailyDealsItem(
                        itemName = productName,
                        priceAfterDiscount = discountedPrice,
                        itemID = productID,
                        isFavourite = isFavourite,
                        quantity = quantity
                    )
                    dailyDealsList.add(dailyDealsItem)
                }
            }
        }
        return dailyDealsList
    }
    fun insertDailyDeals(dailyDeals: List<Pair<Int, Double>>) {
        writableDatabase.beginTransaction()
        try {
            for ((productID, discount) in dailyDeals) {
                val values = ContentValues().apply {
                    put(dailyDeals_table_productID, productID)
                    put(dailyDeals_table_discount, discount)
                }
                writableDatabase.insert(dailyDeals_table, null, values)
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
              P.$Products_table_quantity,
              P.$Products_table_price * CI.$cartItems_table_quantity,
              CI.$cartItems_table_quantity,
              P.$Products_table_productID
       FROM $cartItems_table CI
       INNER JOIN $products_table P ON CI.$cartItems_table_productID = P.$Products_table_productID
    """
        readableDatabase.use { db ->
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val itemName = cursor.getString(cursor.getColumnIndexOrThrow(Products_table_name))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow(Products_table_price))
                    val productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(cartItems_table_quantity))
                    val totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("P.$Products_table_price * CI.$cartItems_table_quantity"))
                    val cardItemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(cartItems_table_quantity))
                    val productId = cursor.getInt(cursor.getColumnIndexOrThrow(cartItems_table_itemID))

                    val cartItem = CartItem(
                        itemName = itemName,
                        price = price,
                        productQuantity = productQuantity,
                        totalPrice = totalPrice,
                        cardItemQuantity = cardItemQuantity,
                        productId = productId
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
    fun insertCartItems(cartItems: List<CartItem>) {
        writableDatabase.use { db ->
            // Begin the transaction
            db.beginTransaction()
            try {
                // Iterate through the list of cart items and insert each one
                for (cartItem in cartItems) {
                    val values = ContentValues().apply {
                        put(cartItems_table_productID, cartItem.productId)
                        put(cartItems_table_quantity, cartItem.cardItemQuantity)
                    }
                    // Insert the cart item
                    db.insertOrThrow(cartItems_table, null, values)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
    //
    fun getAllSubcategoriesByCategory(categoryName: String): List<String> {
        val subcategoriesList = mutableListOf<String>()
        val query = """
        SELECT $subCategories_table_subCategoryName
        FROM $subCategories_table SC
        INNER JOIN $categories_table C ON SC.$subCategories_table_categoryID = C.$categories_table_categoryID
        WHERE C.$categories_table_categoryName = ?
    """
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(categoryName)).use { cursor ->
                while (cursor.moveToNext()) {
                    val subcategoryName = cursor.getString(cursor.getColumnIndexOrThrow(subCategories_table_subCategoryName))
                    subcategoriesList.add(subcategoryName)
                }
            }
        }
        return subcategoriesList
    }
    fun getProductsBySubcategory(subcategoryName: String): List<ResultItem> {
        val productList = mutableListOf<ResultItem>()
        val query = """
        SELECT P.$Products_table_name,
               P.$Products_table_price,
               P.$Products_table_quantity,
               P.$Products_table_productID,
               COUNT(F.$Favourite_table_productID) > 0 as isFavorite,
               brands.$Brands_table_name as brandName
        FROM $products_table P
        INNER JOIN $subCategories_table SC ON P.$Products_table_quantity = SC.$subCategories_table_subCategoryName
        LEFT JOIN $favourite_table F ON P.$Products_table_productID = F.$Favourite_table_productID
        LEFT JOIN $brands_table brands ON P.$Products_table_brandName = brands.$Brands_table_name
        WHERE SC.$subCategories_table_subCategoryName = ?
        GROUP BY P.$Products_table_productID
    """
        readableDatabase.use { db ->
            db.rawQuery(query, arrayOf(subcategoryName)).use { cursor ->
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
    }


}