package com.umega.grocery.shopping.orders.detail

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Filter
import com.umega.grocery.utill.Keys
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import java.sql.Timestamp

class OrderDetailViewModel(private val navController: NavController, private val repo: Repo) : ViewModel() {

    val id = MutableLiveData(0)
    private var order:Order = Order()

    val orderItems:MutableList<OrderItem> = mutableListOf()
    val products = MutableLiveData<MutableList<Product>> (mutableListOf())


    private val orderPrice = MutableLiveData(0.0)

    val orderPriceText: MutableLiveData<String>
        get() = MutableLiveData("EGP ${orderPrice.value}")
    val priceAfterTaxText:MutableLiveData<String>
        get() = MutableLiveData(String.format("EGP %.2f", orderPrice.value!! * 1.14 + 20))
    val address = MutableLiveData<String>("")

    val title :MutableLiveData<String>
        get() = MutableLiveData("Order#${id.value}")

    val shipmentTitle :MutableLiveData<String>
        get() = MutableLiveData("Shipment (${orderItems.size} Items)")

    var date = MutableLiveData<Timestamp>()

    val itemsCount :MutableLiveData<String>
        get() = MutableLiveData("${orderItems.size} Items")


    fun initialize(orderValue: Order){
        order = orderValue
        orderPrice.value = orderValue.totalPrice
        id.value = order.id
        address.value = order.address
        date.value = orderValue.date!!
        orderItems.addAll(repo.getOrderItems(order.id!!))
        repo.retrieveProducts(orderItems.map { it.productID }.toMutableList(), Filter(),products)
    }


    //navigation
    fun navigateToDetail(product:Product){
        val bundle = Bundle().apply {
            putParcelable(Keys.product_detail_bundle_key, product)
        }
        navController.navigate(R.id.action_orderDetailFragment_to_detailItemFragment,bundle)
    }

    fun navigateUp(){
        navController.navigateUp()
    }
}