package com.umega.grocery.utill

data class Product(
    val id:Int,
    val name:String,
    val brandID:Int,
    val price:Double,
    val stockQuantity:Int,
    val subCategoryID: Int,
    val purchaseCount:Int,
    val imgName:String
)
