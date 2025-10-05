
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
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        binding.registerButton.setOnClickListener {
            val nom = binding.nomInput.text.toString()
            val prenom = binding.prenomInput.text.toString()
            val username = binding.usernameInput.text.toString()
            val telephone = binding.telephoneInput.text.toString()
            val pin = binding.pinInput.text.toString()
            
            if (validateInputs(nom, prenom, username, telephone, pin)) {
                viewModel.register(nom, prenom, username, telephone, pin)
            }
        }
        
        binding.loginLink.setOnClickListener {
            finish()
        }
    }
    
    private fun validateInputs(nom: String, prenom: String, username: String, telephone: String, pin: String): Boolean {
        if (nom.isBlank() || prenom.isBlank() || username.isBlank() || telephone.isBlank() || pin.isBlank()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            Toast.makeText(this, "Le code PIN doit être composé de 4 chiffres", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
