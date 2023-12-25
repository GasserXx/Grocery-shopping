package com.umega.grocery.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.auth.LoginViewModelFactory
import com.umega.grocery.databinding.TransistionSignupBinding

class TransitionFragment : Fragment() {
    lateinit var binding : TransistionSignupBinding

    private val navController by lazy { findNavController() }
    private val viewModel: LoginViewModel by viewModels { LoginViewModelFactory(navController) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.transistion_signup,container,false)
        binding.viewModel = viewModel
        return binding.root
    }
}