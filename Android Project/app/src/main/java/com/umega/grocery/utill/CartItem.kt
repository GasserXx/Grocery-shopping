package com.umega.grocery.utill

data class CartItem(
    val itemName: String,
    val price: Double,
    val totalPrice: Double,
    val productId:Int,
    val itemQuantity:Int,
    val imgName:String
)
