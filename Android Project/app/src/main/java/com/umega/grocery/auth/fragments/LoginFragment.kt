package com.umega.grocery.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.umega.grocery.R
import com.umega.grocery.databinding.LoginPageBinding

class LoginFragment :Fragment(){

    lateinit var binding : LoginPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_page,container,false)
        return binding.root
    }

}