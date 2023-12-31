package com.umega.grocery.shopping.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umega.grocery.databinding.OrderHistoryItemBinding
import com.umega.grocery.utill.Order

class OrderHistoryAdapter (private var orders: MutableList<Order>,
                           private val navigateToDetail: (order: Order) -> Unit) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using ViewBinding to inflate the item layout
        val binding: OrderHistoryItemBinding =
            OrderHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.orderName.text = "Order#${order.id}"
        holder.binding.orderDateText.text = "Placed On ${order.date.toString()}"
        holder.binding.root.setOnClickListener { navigateToDetail(order) }
    }


    override fun getItemCount(): Int = orders.size

    class ViewHolder(binding: OrderHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Using ViewBinding for easy access to views
        var binding: OrderHistoryItemBinding
        init {
            this.binding = binding
        }
    }
}