package com.umega.grocery.utill

data class CartItem(
    val itemName: String,
    val price: Double,
    var totalPrice: Double,
    val productId:Int,
    var itemQuantity:Int,
    val imgName:String,
    val discount:Double
)
