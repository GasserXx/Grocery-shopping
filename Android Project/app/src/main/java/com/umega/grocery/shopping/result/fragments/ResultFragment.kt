package com.umega.grocery.shopping.result.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.databinding.ResultPageBinding
import com.umega.grocery.shopping.adapters.ResultAdapter
import com.umega.grocery.shopping.result.ResultViewModel
import com.umega.grocery.shopping.result.ResultViewModelFactory
import com.umega.grocery.utill.Product

class ResultFragment(private val viewModel:ResultViewModel) : Fragment() {
    lateinit var binding : ResultPageBinding
    lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {

        }catch (e:Exception){
            Log.i("LOL", "$e , Failed to init the viewModel")
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.result_page,container,false)
        Log.i("LOL", "viewModel object $viewModel")
        binding.viewModel = viewModel

        // Register orientation change listener
        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                // Update span count when orientation changes
                gridLayoutManager.spanCount = calculateSpanCount()
            }
        }

        //Observing changes in products
        viewModel.products.observe(viewLifecycleOwner){ it1 ->
            viewModel.hideLoading()

            binding.recyclerView.adapter = ResultAdapter(requireContext(), it1, viewModel::navigateToDetail)
            gridLayoutManager =  GridLayoutManager(requireContext(),calculateSpanCount())
            binding.recyclerView.layoutManager = gridLayoutManager

            //updating brands and nationalities values
            viewModel.setBrandsList(getBrands(it1))
        }

        return binding.root
    }
    private fun getBrands(products:MutableList<Product>):MutableList<Int>{
        val lst = mutableListOf<Int>()
        products.forEach { lst.add(it.brandID) }
        return lst
    }

    //calculating the span of the recycleView Grid
    private fun calculateSpanCount(): Int {
        // Calculate span count based on screen width and item width
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        return screenWidth / itemWidth
    }
    companion object{
        const val itemWidth = 360
    }
}
