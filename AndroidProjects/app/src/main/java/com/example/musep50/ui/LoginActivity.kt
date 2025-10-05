package com.example.musep50.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    private val maxPinLength = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPinKeypad()
        setupRegisterLink()
        observeViewModel()
    }

    private fun setupPinKeypad() {
        // Setup number buttons
        val buttons = listOf(
            binding.pinKeypad.findViewById<Button>(R.id.btn0),
            binding.pinKeypad.findViewById<Button>(R.id.btn1),
            binding.pinKeypad.findViewById<Button>(R.id.btn2),
            binding.pinKeypad.findViewById<Button>(R.id.btn3),
            binding.pinKeypad.findViewById<Button>(R.id.btn4),
            binding.pinKeypad.findViewById<Button>(R.id.btn5),
            binding.pinKeypad.findViewById<Button>(R.id.btn6),
            binding.pinKeypad.findViewById<Button>(R.id.btn7),
            binding.pinKeypad.findViewById<Button>(R.id.btn8),
            binding.pinKeypad.findViewById<Button>(R.id.btn9)
        )

        buttons.forEachIndexed { index, button ->
            button?.setOnClickListener {
                onNumberClick(index.toString())
            }
        }

        // Delete button
        binding.pinKeypad.findViewById<Button>(R.id.btnDelete)?.setOnClickListener {
            onDeleteClick()
        }

        // Back button (optional)
        binding.pinKeypad.findViewById<Button>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
    }

    private fun onNumberClick(number: String) {
        if (currentPin.length < maxPinLength) {
            currentPin += number
            updatePinDisplay()

            if (currentPin.length == maxPinLength) {
                // Attempt login
                viewModel.login(binding.usernameInput.text.toString(), currentPin)
            }
        }
    }

    private fun onDeleteClick() {
        if (currentPin.isNotEmpty()) {
            currentPin = currentPin.dropLast(1)
            updatePinDisplay()
        }
    }

    private fun updatePinDisplay() {
        binding.pinDot1.setImageResource(
            if (currentPin.length >= 1) R.drawable.pin_dot_filled else R.drawable.pin_dot_empty
        )
        binding.pinDot2.setImageResource(
            if (currentPin.length >= 2) R.drawable.pin_dot_filled else R.drawable.pin_dot_empty
        )
        binding.pinDot3.setImageResource(
            if (currentPin.length >= 3) R.drawable.pin_dot_filled else R.drawable.pin_dot_empty
        )
        binding.pinDot4.setImageResource(
            if (currentPin.length >= 4) R.drawable.pin_dot_filled else R.drawable.pin_dot_empty
        )
    }

    private fun setupRegisterLink() {
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    // Save user session
                    val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                    prefs.edit().putLong("current_user_id", result.userId).apply()

                    // Navigate to dashboard
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    currentPin = ""
                    updatePinDisplay()
                }
            }
        }
    }
}