package com.umega.grocery.utill

import java.sql.Timestamp

data class Order(
    val id:Int?,
    val voucher: String,
    val totalPrice:Double,
    val address: String,
    val date :Timestamp
)
