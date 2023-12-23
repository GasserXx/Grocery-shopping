package com.umega.grocery.utill

data class ResultItem(
    val productName: String,
    val price:Double,
    val imgName:String,
    val isFavourite: Boolean,
    val productId:Int
)
