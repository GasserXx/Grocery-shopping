package com.umega.grocery.utill

data class ResultItem(
    val productName: String,
    val price: Double,
    val productQuantity: String,
    val productId:Int,
    val isFavorite:Boolean,
    val brandName:String
)
