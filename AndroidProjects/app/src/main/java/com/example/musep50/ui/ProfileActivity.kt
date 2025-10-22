package com.example.musep50.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
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
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { }
            val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
            val userId = prefs.getLong("current_user_id", -1L)
            val key = if (userId != -1L) "profile_image_uri_${'$'}userId" else "profile_image_uri"
            prefs.edit().putString(key, it.toString()).apply()
            binding.profileImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAppearancePrefs()

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

        // Load profile photo per user if previously set
        val photoKey = if (userId != -1L) "profile_image_uri_${'$'}userId" else "profile_image_uri"
        var photoUriString = sharedPreferences.getString(photoKey, null)
        if (photoUriString.isNullOrBlank() && userId != -1L) {
            // Migration: use legacy generic key if present, then move it to per-user key
            val legacy = sharedPreferences.getString("profile_image_uri", null)
            if (!legacy.isNullOrBlank()) {
                sharedPreferences.edit().putString(photoKey, legacy).apply()
                photoUriString = legacy
            }
        }
        if (!photoUriString.isNullOrBlank()) {
            binding.profileImage.setImageURI(Uri.parse(photoUriString))
        }

        if (userId != -1L) {
            viewModel.loadUserById(userId)
        }
    }

    private fun setupButtons() {
        binding.profileImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }
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
                        // If email changed, migrate stored PIN to new key
                        val oldEmail = user.email
                        val newEmail = inputEmail.text.toString()
                        if (newEmail.isNotBlank() && newEmail != oldEmail) {
                            val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                            val oldPinKey = "pin_${oldEmail}"
                            val newPinKey = "pin_${newEmail}"
                            val existingPin = prefs.getString(oldPinKey, null)
                            if (existingPin != null) {
                                prefs.edit().putString(newPinKey, existingPin).remove(oldPinKey).apply()
                            }
                            // Update current_user_email if this is the current account
                            val currentId = prefs.getLong("current_user_id", -1L)
                            if (currentId == user.id) {
                                prefs.edit().putString("current_user_email", newEmail).apply()
                            }
                        }
                        repo.updateUser(user.copy(nom = inputName.text.toString(), email = newEmail))
                        viewModel.loadUserById(user.id)
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        binding.btnChangePin.setOnClickListener {
            val context = this
            val inputPin = EditText(context).apply { hint = "Nouveau code PIN (4 chiffres)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD }
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
                    var userEmail = viewModel.currentUser.value?.email
                    val currentUser = viewModel.currentUser.value
                    if (userEmail.isNullOrBlank()) {
                        val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                        userEmail = prefs.getString("current_user_email", null)
                    }
                    val isFourDigits = p1.matches(Regex("^\\d{4}$"))
                    if (userEmail.isNullOrBlank() || currentUser == null) {
                        android.widget.Toast.makeText(this, "Utilisateur introuvable", android.widget.Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (!isFourDigits) {
                        android.widget.Toast.makeText(this, "Le PIN doit contenir exactement 4 chiffres", android.widget.Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (p1 != p2) {
                        android.widget.Toast.makeText(this, "Les deux codes PIN ne correspondent pas", android.widget.Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    // Update DB PIN, then update biometric copy
                    lifecycleScope.launch {
                        try {
                            val db = AppDatabase.getDatabase(this@ProfileActivity)
                            val repo = Repository(db)
                            repo.updateUser(currentUser.copy(pin = p1))
                            val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
                            prefs.edit().putString("pin_${userEmail}", p1).apply()
                            android.widget.Toast.makeText(this@ProfileActivity, "Code PIN mis à jour", android.widget.Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(this@ProfileActivity, "Erreur lors de la mise à jour du PIN", android.widget.Toast.LENGTH_SHORT).show()
                        }
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
            // Only clear session-related keys, keep persistent user data like profile images and PINs
            sharedPreferences.edit()
                .remove("current_user_id")
                .remove("current_user_email")
                .apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}