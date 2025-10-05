
package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.R
import com.example.musep50.databinding.ActivityLoginBinding
import com.example.musep50.viewmodel.AuthViewModel
import com.example.musep50.viewmodel.LoginResult

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private var currentPin = ""
    private var currentEmail = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupEmailStep()
        setupPinStep()
        observeViewModel()
    }
    
    private fun setupPinStep() {
        val pinButtons = listOf(
            binding.pinLayout.btn0,
            binding.pinLayout.btn1,
            binding.pinLayout.btn2,
            binding.pinLayout.btn3,
            binding.pinLayout.btn4,
            binding.pinLayout.btn5,
            binding.pinLayout.btn6,
            binding.pinLayout.btn7,
            binding.pinLayout.btn8,
            binding.pinLayout.btn9
        )
        
        pinButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                addPinDigit(index.toString())
            }
        }
        
        binding.pinLayout.btnDelete.setOnClickListener {
            removePinDigit()
        }
        
        binding.pinLayout.btnBack.setOnClickListener {
            showEmailStep()
        }
    }
    
    private fun setupEmailStep() {
        binding.continueButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty()) {
                currentEmail = email
                viewModel.checkUserExists(email)
            } else {
                Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun addPinDigit(digit: String) {
        if (currentPin.length < 4) {
            currentPin += digit
            updatePinDots()
            
            if (currentPin.length == 4) {
                viewModel.login(currentEmail, currentPin)
            }
        }
    }
    
    private fun removePinDigit() {
        if (currentPin.isNotEmpty()) {
            currentPin = currentPin.dropLast(1)
            updatePinDots()
        }
    }
    
    private fun updatePinDots() {
        val dots = listOf(
            binding.pinLayout.pinDot1,
            binding.pinLayout.pinDot2,
            binding.pinLayout.pinDot3,
            binding.pinLayout.pinDot4
        )
        
        dots.forEachIndexed { index, dot ->
            if (index < currentPin.length) {
                dot.setBackgroundResource(R.drawable.pin_dot_filled)
            } else {
                dot.setBackgroundResource(R.drawable.pin_dot_empty)
            }
        }
    }
    
    private fun showEmailStep() {
        binding.emailInputLayout.visibility = View.VISIBLE
        binding.pinLayout.root.visibility = View.GONE
        binding.continueButton.visibility = View.VISIBLE
        binding.registerButton.visibility = View.VISIBLE
        binding.subtitleText.text = "Entrez votre adresse email"
        currentPin = ""
        updatePinDots()
    }
    
    private fun showPinStep() {
        binding.emailInputLayout.visibility = View.GONE
        binding.pinLayout.root.visibility = View.VISIBLE
        binding.continueButton.visibility = View.GONE
        binding.registerButton.visibility = View.GONE
        binding.subtitleText.text = "Entrez votre code PIN"
    }
    
    private fun observeViewModel() {
        viewModel.userExists.observe(this) { exists ->
            if (exists) {
                showPinStep()
            } else {
                Toast.makeText(this, "Utilisateur introuvable. Veuillez vous inscrire.", Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                    sharedPreferences.edit().putLong("current_user_id", result.user.id).apply()
                    
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    currentPin = ""
                    updatePinDots()
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
