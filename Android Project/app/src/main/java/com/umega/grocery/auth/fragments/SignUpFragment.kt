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
import com.umega.grocery.databinding.SignupPageBinding

class SignUpFragment : Fragment() {
    lateinit var binding : SignupPageBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        binding = DataBindingUtil.inflate(inflater, R.layout.signup_page,container,false)
        binding.viewModel = viewModel


        viewModel.response.observe(viewLifecycleOwner){
            if (it != -3)
                viewModel.afterSignUp()
        }

        //observing error raising
        viewModel.firstNameError.observe(viewLifecycleOwner){
            binding.firstNameEdit.error = it
        }
        viewModel.lastNameError.observe(viewLifecycleOwner){
            binding.lastNameEdit.error = it
        }
        viewModel.emailError.observe(viewLifecycleOwner){
            binding.emailAddressEdit.error = it
        }
        viewModel.passwordError.observe(viewLifecycleOwner){
            binding.passwordEdit.error = it
        }
        viewModel.phoneNumberError.observe(viewLifecycleOwner){
            binding.editTextPhone.error = it
        }
        return binding.root
    }

}