package com.umega.grocery.shopping.result.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.DetailItemPageBinding
import com.umega.grocery.databinding.ResultPageBinding

class ResultFragment : Fragment() {
    lateinit var binding : ResultPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.result_page,container,false)
        return binding.root
    }
}