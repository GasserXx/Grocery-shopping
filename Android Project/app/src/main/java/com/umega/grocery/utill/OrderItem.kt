package com.umega.grocery.utill

data class OrderItem(
    val orderID: Int,
    val productID: Int,
    val price: Double,
    val discount: Double,
    val quantity: Int
)
