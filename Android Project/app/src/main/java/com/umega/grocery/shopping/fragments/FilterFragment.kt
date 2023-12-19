package com.umega.grocery.shopping.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.DetailItemPageBinding
import com.umega.grocery.databinding.FilterPageBinding

class FilterFragment : Fragment() {
    lateinit var binding : FilterPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.filter_page,container,false)
        return binding.root
    }
}