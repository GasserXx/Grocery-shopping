package com.umega.grocery.shopping.orders.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.OrderDetailPageBinding
import com.umega.grocery.databinding.OrderHistoryPageBinding
import com.umega.grocery.shopping.adapters.OrderDetailAdapter
import com.umega.grocery.shopping.adapters.OrderHistoryAdapter
import com.umega.grocery.utill.Keys
import com.umega.grocery.utill.Order
import com.umega.grocery.utill.OrderItem
import java.sql.Timestamp

class OrderDetailFragment : Fragment() {
    lateinit var binding : OrderDetailPageBinding
    private lateinit var viewModel: OrderDetailViewModel
    lateinit var order :Order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = OrderDetailViewModel(findNavController(), Repo(requireContext()))

        //init values assignment
        val args = arguments
        order = if (args != null) {
            Order(
                args.getInt(Keys.order_id_bundle_key),
                "",
                args.getDouble(Keys.total_price_bundle_key),
                args.getString(Keys.address_bundle_key,"error Address"),
                Timestamp( args.getLong(Keys.date_bundle_key))
            )
        } else {
            Order()
        }
        viewModel.initialize(order)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.order_detail_page, container, false)
        binding.viewModel = viewModel

        viewModel.products.observe(viewLifecycleOwner) {
            binding.itemsRecycleView.adapter =
                OrderDetailAdapter(viewModel.orderItems, viewModel.products.value!!, requireContext(), viewModel::navigateToDetail)
            binding.itemsRecycleView.layoutManager = LinearLayoutManager(requireContext())
        }
        return binding.root
    }
}