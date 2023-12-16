package com.piyal.rapidfeast.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.piyal.rapidfeast.R
import com.piyal.rapidfeast.data.model.LoginRequest
import com.piyal.rapidfeast.databinding.ActivityLogInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
        observeLoginStatus()
    }

    private fun initView() {
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            val phoneNo = binding.editPhone.text.toString()
            if (phoneNo.isNotEmpty() && phoneNo.length == 10) {
                viewModel.login(LoginRequest(phoneNo))
            } else {
                Toast.makeText(applicationContext, "Invalid Phone Number!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLoginStatus() {
        viewModel.performLoginStatus.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    // Handle success, navigate to the next screen, etc.
                    val userPlaceModel = resource.data
                    // Example: Navigate to HomeActivity
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finish()
                }

                Status.LOADING -> {
                    // Show loading indicator
                }

                Status.ERROR -> {
                    // Handle error, show an error message, etc.
                    val errorMessage = resource.message ?: "An error occurred"
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }

                Status.OFFLINE_ERROR -> {
                    // Handle offline error
                    Toast.makeText(applicationContext, "Offline error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
