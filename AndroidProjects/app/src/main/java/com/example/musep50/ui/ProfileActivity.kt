package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musep50.data.AppDatabase
import com.example.musep50.data.Repository
import com.example.musep50.databinding.ActivityProfileBinding
import com.example.musep50.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

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

        viewModel.currentUser.observe(this) { user ->
            user?.let {
                binding.userName.text = it.nom
                binding.userEmail.text = it.email
            }
        }

        if (userId != -1L) {
            viewModel.loadUserById(userId)
        }
    }

    private fun setupButtons() {
        binding.btnEditProfile.setOnClickListener {
            val user = viewModel.currentUser.value ?: return@setOnClickListener
            val context = this
            val container = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)
            val inputName = EditText(context).apply { hint = "Nom"; setText(user.nom) }
            val inputEmail = EditText(context).apply { hint = "Email"; setText(user.email) }

            val holder = androidx.core.widget.NestedScrollView(context).apply {
                addView(android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    val pad = (16 * resources.displayMetrics.density).toInt()
                    setPadding(pad, pad, pad, pad)
                    addView(inputName)
                    addView(inputEmail)
                })
            }

            AlertDialog.Builder(this)
                .setTitle("Modifier le profil")
                .setView(holder)
                .setPositiveButton("Enregistrer") { _, _ ->
                    lifecycleScope.launch {
                        val db = AppDatabase.getDatabase(context)
                        val repo = Repository(db)
                        repo.updateUser(user.copy(nom = inputName.text.toString(), email = inputEmail.text.toString()))
                        viewModel.loadUserById(user.id)
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        binding.btnChangePin.setOnClickListener {
            val context = this
            val inputPin = EditText(context).apply { hint = "Nouveau code PIN"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD }
            val inputConfirm = EditText(context).apply { hint = "Confirmer"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD }
            val layout = androidx.core.widget.NestedScrollView(context).apply {
                addView(android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    val pad = (16 * resources.displayMetrics.density).toInt()
                    setPadding(pad, pad, pad, pad)
                    addView(inputPin)
                    addView(inputConfirm)
                })
            }
            AlertDialog.Builder(this)
                .setTitle("Changer le code PIN")
                .setView(layout)
                .setPositiveButton("Valider") { _, _ ->
                    val p1 = inputPin.text.toString()
                    val p2 = inputConfirm.text.toString()
                    if (p1.isNotBlank() && p1 == p2) {
                        val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                        prefs.edit().putString("user_pin", p1).apply()
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        binding.btnAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("À propos")
                .setMessage("MUSEP50\nVersion 1.0\n\nApplication de gestion des cotisations et opérations.")
                .setPositiveButton("OK", null)
                .show()
        }

        binding.btnLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}