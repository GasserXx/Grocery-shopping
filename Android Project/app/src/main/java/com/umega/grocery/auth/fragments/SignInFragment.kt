package com.umega.grocery.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.umega.grocery.R
import com.umega.grocery.databinding.SigninPageBinding
import com.umega.grocery.databinding.SignupPageBinding

class SignInFragment : Fragment() {
    lateinit var binding : SigninPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.signin_page,container,false)
        return binding.root
    }
}