package com.umega.grocery.shopping

import ImageHandle
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.umega.grocery.R
import com.umega.grocery.databinding.DetailItemPageBinding
import com.umega.grocery.shopping.result.ResultViewModel
import com.umega.grocery.shopping.result.ResultViewModelFactory
import com.umega.grocery.utill.Keys
import com.umega.grocery.utill.Product
import kotlinx.coroutines.runBlocking


class DetailItemFragment : Fragment() {
    private lateinit var imageHandle:ImageHandle
    lateinit var binding : DetailItemPageBinding
    private val navController by lazy { findNavController() }
    private val viewModel: DetailItemViewModel by viewModels { DetailItemViewModelFactory(navController,requireContext()) }
   //
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imageHandle  = ImageHandle(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_item_page,container,false)
        binding.viewModel = viewModel
        val product = arguments?.getParcelable<Product>(Keys.product_detail_bundle_key)
        viewModel.initialization(product!!)
        viewModel.productLiveData.observe(viewLifecycleOwner) { product ->
            binding.product = product
            binding.executePendingBindings()
            loadImage(product.imgName, binding.itemImage)
        }
        viewModel.productQuantity.observe(viewLifecycleOwner, Observer { quantity ->
            // Update the UI with the new quantity value
            binding.cartQuantityText.text = quantity.toString()
        })
        checkIfItemDeals(product)
        return binding.root
    }
    private fun checkIfItemDeals(product:Product){
        if(product.discount>0.0){
            binding.priceBeforeText.visibility= View.VISIBLE
        }
    }
    private fun loadImage(fileName: String, imageView: ImageView) {
        runBlocking {
            val filePath = imageHandle.getCachedFilePath(fileName)
            // Load the image using Glide or set a broken image if the file doesn't exist
            if (filePath != null) {
                Glide.with(requireContext())
                    .load(filePath)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.corrupt)
            }
        }
    }

}