package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.databinding.ActivityProfileBinding
import com.example.musep50.viewmodel.AuthViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserProfile()
        setupButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("current_user_id", -1L)

        viewModel.getUserById(userId).observe(this) { user ->
            user?.let {
                binding.userName.text = it.nom
                binding.userEmail.text = it.email
            }
        }
    }

    private fun setupButtons() {
        binding.btnEditProfile.setOnClickListener {
            // TODO: Open edit profile activity
        }

        binding.btnChangePin.setOnClickListener {
            // TODO: Open change PIN dialog
        }

        binding.btnAbout.setOnClickListener {
            // TODO: Show about dialog
        }

        binding.btnLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}