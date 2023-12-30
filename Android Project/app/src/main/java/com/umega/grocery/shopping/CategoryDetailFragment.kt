package com.umega.grocery.shopping

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.databinding.CategoryDetailPageBinding
import com.umega.grocery.shopping.adapters.CategoryAdapter
import com.umega.grocery.shopping.adapters.SubCategoryAdapter

class CategoryDetailFragment : Fragment() {
    lateinit var binding : CategoryDetailPageBinding
    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by activityViewModels { HomeViewModelFactory(navController,requireContext()) }
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.category_detail_page,container,false)
        binding.viewModel = viewModel
        val listView: ListView = binding.subCategoriesList
        subCategoryAdapter = SubCategoryAdapter()
        listView.adapter = subCategoryAdapter
        try{
            viewModel.getSubCategoriesList().observe(viewLifecycleOwner) { items -> subCategoryAdapter.submitList(items) }
            Log.i("lolintry","")
        }catch (e:Exception){
            Log.i("lol",e.toString())
        }

        return binding.root
    }
}