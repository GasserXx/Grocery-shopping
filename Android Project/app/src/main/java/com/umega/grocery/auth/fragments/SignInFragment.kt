package com.umega.grocery.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.umega.grocery.R
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.databinding.SigninPageBinding

class SignInFragment : Fragment() {
    lateinit var binding : SigninPageBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.signin_page,container,false)
        binding.viewModel = viewModel

        viewModel.emailError.observe(viewLifecycleOwner){
            binding.emailAddressEdit.error = it
        }
        viewModel.passwordError.observe(viewLifecycleOwner){
            binding.passwordEdit.error = it
        }
        viewModel.response.observe(viewLifecycleOwner){
            if (it != -3)
                viewModel.afterLogin()
        }
        return binding.root
    }
}