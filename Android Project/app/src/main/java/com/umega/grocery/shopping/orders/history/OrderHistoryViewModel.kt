package com.umega.grocery.shopping.orders.history

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Keys
import com.umega.grocery.utill.Order
import kotlinx.coroutines.runBlocking

class OrderHistoryViewModel (private val navController: NavController, private val repo: Repo) : ViewModel() {

    private val orders :MutableList<Order> = repo.getAllStoredOrders()

    fun getOrders():MutableList<Order> = orders


    //navigation
    fun navigateUp(){
        navController.navigateUp()
    }

    fun navigateToDetail(order: Order){
        val bundle = Bundle().apply {
            putInt(Keys.order_id_bundle_key,order.id?:-1)
            putDouble(Keys.total_price_bundle_key, order.totalPrice)
            putString(Keys.address_bundle_key,order.address)
            putLong(Keys.date_bundle_key, order.date!!.time)
        }
        navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment, bundle)
    }

    fun refreshOrders() {
        runBlocking {
            repo.refreshOrderAndOrderItemsTables()
        }
    }
}