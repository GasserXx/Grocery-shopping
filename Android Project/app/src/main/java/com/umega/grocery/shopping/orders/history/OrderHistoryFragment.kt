package com.umega.grocery.shopping.orders.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.OrderHistoryPageBinding
import com.umega.grocery.shopping.adapters.OrderHistoryAdapter

class OrderHistoryFragment:Fragment() {
    lateinit var binding : OrderHistoryPageBinding
    private lateinit var viewModel: OrderHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = OrderHistoryViewModel(findNavController(), Repo(requireContext()))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.order_history_page, container, false)
        binding.viewModel = viewModel
        viewModel.getOrders().forEach { Log.i("LOL", "Order Id retrieved: ${it.id}") }
        //handling recycle View
        binding.ordersRecycleView.adapter = OrderHistoryAdapter(viewModel.getOrders(), viewModel::navigateToDetail)
        binding.ordersRecycleView.layoutManager =  LinearLayoutManager(requireContext())

        return binding.root
    }
}