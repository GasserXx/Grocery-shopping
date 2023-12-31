package com.umega.grocery.shopping.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umega.grocery.R
import com.umega.grocery.databinding.CartPageBinding
import com.umega.grocery.shopping.main.HomeViewModelFactory
import com.umega.grocery.shopping.adapters.CartAdapter
import com.umega.grocery.shopping.main.HomeViewModel


class CartFragment : Fragment() {
    lateinit var binding : CartPageBinding
    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by activityViewModels { HomeViewModelFactory(navController,requireContext()) }
    private lateinit var cartAdapter: CartAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.cart_page, container, false)
        binding.viewModel = viewModel
        viewModel.getAllCartItems()
        val cartRecycleView: RecyclerView = binding.cartRecycleView
        cartRecycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val cartAdapter = CartAdapter(requireContext(),viewModel) // Initialize with an empty list
        viewModel.getCartItemsList().observe(viewLifecycleOwner) { cartItemsList ->
            // Update the adapter with the new list when it changes
            cartAdapter.submitList(cartItemsList)
        }
        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.totalPriceText.text = totalPrice
        }
        viewModel.navigate_to_result.observe(viewLifecycleOwner){
            if (it) {
                viewModel.hideLoading()
                viewModel.showMessage("Order Placed")
                viewModel.navigateToDetailOrder()
            }
        }
        cartRecycleView.adapter = cartAdapter
        return binding.root
    }
}