package com.example.musep50.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.musep50.R
import com.example.musep50.databinding.ActivityLoginBinding
import com.example.musep50.viewmodel.AuthViewModel
import com.example.musep50.viewmodel.LoginResult

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private var currentPin = ""
    private var currentEmail = ""
    private var isEmailStep = true
    private lateinit var sharedPreferences: SharedPreferences
    
    private val pinDots by lazy {
        listOf(
            binding.pinDot1,
            binding.pinDot2,
            binding.pinDot3,
            binding.pinDot4
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.continueButton.setOnClickListener {
            if (isEmailStep) {
                val email = binding.emailInput.text.toString().trim()
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    currentEmail = email
                    showPinStep()
                } else {
                    Toast.makeText(this, "Veuillez entrer un email valide", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        setupPinKeypad()
    }
    
    private fun setupPinKeypad() {
        val numberButtons = listOf(
            binding.btn0 to "0",
            binding.btn1 to "1",
            binding.btn2 to "2",
            binding.btn3 to "3",
            binding.btn4 to "4",
            binding.btn5 to "5",
            binding.btn6 to "6",
            binding.btn7 to "7",
            binding.btn8 to "8",
            binding.btn9 to "9"
        )
        
        numberButtons.forEach { (button, number) ->
            button.setOnClickListener {
                if (currentPin.length < 4) {
                    currentPin += number
                    updatePinDots()
                    
                    if (currentPin.length == 4) {
                        attemptLogin()
                    }
                }
            }
        }
        
        binding.btnDelete.setOnClickListener {
            if (currentPin.isNotEmpty()) {
                currentPin = currentPin.dropLast(1)
                updatePinDots()
            }
        }
        
        binding.btnBack.setOnClickListener {
            showEmailStep()
        }
    }
    
    private fun updatePinDots() {
        pinDots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                if (index < currentPin.length) R.drawable.pin_dot_filled
                else R.drawable.pin_dot_empty
            )
        }
    }
    
    private fun showEmailStep() {
        isEmailStep = true
        binding.emailInputLayout.visibility = View.VISIBLE
        binding.pinLayout.visibility = View.GONE
        binding.continueButton.visibility = View.VISIBLE
        binding.registerButton.visibility = View.VISIBLE
        binding.titleText.text = "Connexion"
        binding.subtitleText.text = "Entrez votre adresse email"
        currentPin = ""
        updatePinDots()
    }
    
    private fun showPinStep() {
        isEmailStep = false
        binding.emailInputLayout.visibility = View.GONE
        binding.pinLayout.visibility = View.VISIBLE
        binding.continueButton.visibility = View.GONE
        binding.registerButton.visibility = View.GONE
        binding.titleText.text = "Code PIN"
        binding.subtitleText.text = "Entrez votre code PIN"
        currentPin = ""
        updatePinDots()
    }
    
    private fun attemptLogin() {
        viewModel.login(currentEmail, currentPin)
    }
    
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, "Bienvenue ${result.user.nom}", Toast.LENGTH_SHORT).show()
                    
                    sharedPreferences.edit()
                        .putLong("current_user_id", result.user.id)
                        .putString("current_user_email", result.user.email)
                        .putString("current_user_nom", result.user.nom)
                        .apply()
                    
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    currentPin = ""
                    updatePinDots()
                }
            }
        }
    }
}
