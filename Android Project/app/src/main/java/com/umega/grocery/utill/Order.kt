package com.umega.grocery.utill

import java.sql.Timestamp

data class Order(
    var id:Int? = null,
    val voucher: String = "",
    val totalPrice:Double = 0.0,
    val address: String = "",
    val date :Timestamp = Timestamp(System.currentTimeMillis())
)
