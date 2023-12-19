package com.umega.grocery.dataBase
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.umega.grocery.utill.DailyDealsItem

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "GroceryShopping"
        const val DATABASE_VERSION = 1
        //define table names
        const val shoppingCart_table = "ShoppingCart"
        const val cartItems_table = "Cart_Items"
        const val products_table = "Products"
        const val brands_table = "Brands"
        const val favourite_table = "Favourite"
        const val dailyDeals_table = "Daily_Deals"
        //define user_table items
        const val  user_table_userID ="UserID"
        const val user_table_username  ="Username"
        const val  user_table_password ="Password"
        const val  user_table_email ="Email"
        //define shoppingCart_table
        const val  shoppingCart_table_cartID ="CartID"
        const val shoppingCart_table_userID  ="UserID"
        const val  shoppingCart_table_totalPrice ="TotalPrice"
        //define cartItems_table
        const val  cartItems_table_itemID ="ItemID"
        const val cartItems_table_cartID  ="CartID"
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
        //define Favourite_table
        const val  Favourite_table_productID ="ProductID"
        //define dailyDeals_table
        const val  dailyDeals_table_productID ="ProductID"
        const val  dailyDeals_table_discount ="Discount"

    }
    //create tables

    private val  createTableShoppingCart= """
            CREATE TABLE IF NOT EXISTS $shoppingCart_table (
                $shoppingCart_table_cartID INTEGER PRIMARY KEY AUTOINCREMENT,
                $shoppingCart_table_userID INTEGER NOT NULL,
                $shoppingCart_table_totalPrice REAL
            );
        """
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
                $cartItems_table_cartID INTEGER NOT NULL,
                $cartItems_table_productID INTEGER NOT NULL,
                $cartItems_table_quantity INTEGER NOT NULL,
                FOREIGN KEY($cartItems_table_cartID) REFERENCES $shoppingCart_table($shoppingCart_table_cartID),
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
                $dailyDeals_table_productID INTEGER PRIMARY KEY,
                $dailyDeals_table_discount REAL,
                FOREIGN KEY($dailyDeals_table_productID) REFERENCES $products_table($Products_table_productID)
            );
        """
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableShoppingCart)
        db?.execSQL(createTableCartItems)
        db?.execSQL(createTableProducts)
        db?.execSQL(createTableBrands)
        db?.execSQL(createTableDailyDeals)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
    fun getAllDailyDeals(): List<DailyDealsItem> {
        val dailyDealsList = mutableListOf<DailyDealsItem>()
        val isFavourite = 0
        val query = """
       SELECT P.$Products_table_name, 
              P.$Products_table_price, 
              DD.$dailyDeals_table_discount,
              P.$Products_table_productID,
              COUNT(F.$Favourite_table_productID) > 0 as $isFavourite,
              CI.$cartItems_table_quantity
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
                    val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(isFavourite.toString())) == 1
                    val quantity = cursor.getString(cursor.getColumnIndexOrThrow(cartItems_table_quantity))
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
    fun insertDailyDeal(productID: Int, discount: Double) {
        val values = ContentValues().apply {
            put(dailyDeals_table_productID, productID)
            put(dailyDeals_table_discount, discount)
        }
        writableDatabase.use { db ->
            db.insert(dailyDeals_table, null, values)
        }
    }
}