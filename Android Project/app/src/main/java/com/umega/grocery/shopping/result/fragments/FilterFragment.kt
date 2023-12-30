package com.umega.grocery.shopping.result.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.FilterPageBinding
import com.umega.grocery.shopping.adapters.BrandAdapter
import com.umega.grocery.shopping.adapters.NationalityAdapter
import com.umega.grocery.shopping.result.ResultViewModel

class FilterFragment(private val viewModel:ResultViewModel) : Fragment() {
    lateinit var binding : FilterPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.filter_page,container,false)
        binding.viewModel = viewModel

        viewModel.products.observe(viewLifecycleOwner) {
            val brandAdapter = BrandAdapter(requireContext(), viewModel.brands.toList(), viewModel)
            binding.brandListView.adapter = brandAdapter

            val nationalityAdapter =
                NationalityAdapter(requireContext(), viewModel.nationalities.toList(), viewModel)
            binding.nationalityListView.adapter = nationalityAdapter

        }
        return binding.root
    }
}