package com.umega.grocery.shopping.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModelFactory
import com.umega.grocery.databinding.HomePageBinding
import com.umega.grocery.databinding.LoginPageBinding
import com.umega.grocery.shopping.fragments.CategoryAdapter
import com.umega.grocery.shopping.fragments.HomeViewModel
import com.umega.grocery.shopping.fragments.HomeViewModelFactory


class HomeFragment : Fragment() {
    lateinit var binding : HomePageBinding
    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by viewModels { HomeViewModelFactory(navController,requireContext()) }
    private lateinit var categoryAdapter: CategoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_page,container,false)
        binding.viewModel = viewModel
        Log.i("lol5","iam in home fragment")
        categoryAdapter = CategoryAdapter(requireContext())
       val gridView: GridView = binding.categoryMenuGridView
        gridView.adapter = categoryAdapter
        try{
            viewModel.getItemList().observe(viewLifecycleOwner) { items -> categoryAdapter.submitList(items) }
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
        return binding.root
    }
}