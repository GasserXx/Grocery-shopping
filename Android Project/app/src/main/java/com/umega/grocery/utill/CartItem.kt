package com.umega.grocery.utill

data class CartItem(
    val itemName: String,
    val price: Double,
    val productQuantity: String,
    val totalPrice: Double,
    val cardItemQuantity: Int,
    val productId:Int
)
