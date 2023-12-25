package com.umega.grocery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.umega.grocery.auth.LoginViewModel
import com.umega.grocery.auth.LoginViewModelFactory
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: LoginViewModel by viewModels { LoginViewModelFactory(navController) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        //init repo
        viewModel.setRepo(Repo(context = applicationContext))

        //check if userSigned out
        checkLog()

        // Observe the LiveData in the ViewModel
        viewModel.messageLiveData.observe(this) { message ->
            // Update the UI with the message
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.authenticated.observe(this){
            Log.i(TAG, "change got noticed in login activity")
            if (it)
                endActivity()
        }
        viewModel.loading.observe(this){
            if(it)
                showLoading()
            else
                hideLoading()
        }
    }

    private fun checkLog() {
        if (viewModel.checkUserIdStorage())
            endActivity()
    }

    private fun showLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.loadingProgressBar.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.GONE
    }
    private fun endActivity(){
        // Create an Intent to start the new activity
        val intent = Intent(this, MainActivity::class.java)
        // Start the new activity
        startActivity(intent)
        // Finish the current activity to close it
        finish()
    }
    companion object{
        const val TAG = "LOGIN ACTIVITY CLASS"
    }
}