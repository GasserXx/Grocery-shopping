package com.umega.grocery.shopping.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umega.grocery.R
import com.umega.grocery.databinding.HomePageBinding
import com.umega.grocery.shopping.HomeViewModel
import com.umega.grocery.shopping.HomeViewModelFactory
import com.umega.grocery.shopping.adapters.CategoryAdapter
import com.umega.grocery.shopping.adapters.DailyDealsAdapter
import com.umega.grocery.shopping.adapters.StoreDealsAdapter


class HomeFragment : Fragment() {
    lateinit var binding : HomePageBinding
    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by activityViewModels { HomeViewModelFactory(navController,requireContext()) }
    private lateinit var categoryAdapter: CategoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_page,container,false)
        binding.viewModel = viewModel
        //category grid view handle and it button
        categoryAdapter = CategoryAdapter(requireContext()) { category ->
            viewModel.onCategoryItemClick(category)
        }
       val gridView: GridView = binding.categoryMenuGridView
        gridView.adapter = categoryAdapter

        //TODO make them Static "No Connection is necessary for a static list"
        try{
            viewModel.getCategoriesList().observe(viewLifecycleOwner) { items -> categoryAdapter.submitList(items) }

        }catch (e:Exception){
            Log.i("lol",e.toString())
        }
        binding.categoriesButton.setOnClickListener {
            val params = binding.categoryDropMenu.layoutParams
            if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                // If the current height is WRAP_CONTENT, set it to your desired value (e.g., 180dp)
                params.height = resources.getDimensionPixelSize(R.dimen.your_desired_height)
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            binding.categoryDropMenu.layoutParams = params
        }
        // daily deals recycle view handle
        val appMemberDealsRecyclerView: RecyclerView = binding.appMemberDealsRecyclerView
        appMemberDealsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val dailyAdapter = DailyDealsAdapter(requireContext()) { product ->
            val bundle = Bundle()
            bundle.putParcelable("productKey", product)
            Log.i("lolsend",product.toString())
            navController.navigate(R.id.action_mainPageContainer_to_detailItemFragment,bundle)
        }
        appMemberDealsRecyclerView.adapter = dailyAdapter
        viewModel.getDailyDealsList().observe(viewLifecycleOwner) {
                items -> dailyAdapter.submitList(items) }
        // store deals recycle view handle
        val appDealsRecyclerView: RecyclerView = binding.appDealsRecyclerView
        appDealsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val storeAdapter = StoreDealsAdapter(requireContext()) { product ->
            val bundle = Bundle()
            bundle.putParcelable("productKey", product)
            Log.i("lolsend",product.toString())
            navController.navigate(R.id.action_mainPageContainer_to_detailItemFragment,bundle)
        }
        appDealsRecyclerView.adapter = storeAdapter
        viewModel.getStoreDealsList().observe(viewLifecycleOwner) {
                items -> storeAdapter.submitList(items) }
        return binding.root
    }
}