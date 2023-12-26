package com.umega.grocery.shopping.result.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModelFactory
import com.umega.grocery.databinding.ResultPageContainerBinding
import com.umega.grocery.shopping.result.ResultViewModel

class ResultContainer : Fragment() {
    lateinit var binding : ResultPageContainerBinding

    private val navController by lazy { findNavController() }
    private val viewModel: ResultViewModel by viewModels { LoginViewModelFactory(navController) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.result_page_container,container,false)
        binding.viewModel = viewModel
        return binding.root
    }
}