package com.umega.grocery.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.FilterPageBinding
import com.umega.grocery.databinding.SearchPageBinding

class Fragment : Fragment() {
    lateinit var binding : SearchPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_page,container,false)
        return binding.root
    }
}