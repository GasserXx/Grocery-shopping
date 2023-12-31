package com.umega.grocery.shopping

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.DetailItemPageBinding
import com.umega.grocery.utill.Product


class DetailItemFragment : Fragment() {
    lateinit var binding : DetailItemPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_item_page,container,false)
        val product = arguments?.getParcelable<Product>("productKey")
        Log.i("lolreceved",product.toString())
        return binding.root
    }
}