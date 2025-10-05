package com.example.musep50.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.databinding.ActivityRegisterBinding
import com.example.musep50.viewmodel.AuthViewModel
import com.example.musep50.viewmodel.RegisterResult

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val nom = binding.nomInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val pin = binding.pinInput.text.toString().trim()

            if (nom.isBlank() || email.isBlank() || pin.length != 4) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(nom, email, null, null, pin)
        }

        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
