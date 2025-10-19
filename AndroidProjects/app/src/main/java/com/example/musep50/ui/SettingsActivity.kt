package com.example.musep50.ui

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.musep50.databinding.ActivitySettingsBinding
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        applyAppearancePrefs()
        loadPrefs()
        setupHandlers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadPrefs() {
        val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        val dark = prefs.getBoolean("pref_dark_mode", false)
        val notif = prefs.getBoolean("pref_notifications", true)
        val exportPath = prefs.getString("pref_export_dir", defaultExportPath())
        val edgeToEdge = prefs.getBoolean("pref_edge_to_edge", false)
        val bgStyle = prefs.getString("pref_bg_style", "default")

        binding.switchDarkMode.isChecked = dark
        binding.switchNotifications.isChecked = notif
        binding.exportPath.text = exportPath
        binding.switchEdgeToEdge.isChecked = edgeToEdge

        when (bgStyle) {
            "orange_white" -> binding.bgStyleGroup.check(binding.radioBgOrangeWhite.id)
            "blue_orange" -> binding.bgStyleGroup.check(binding.radioBgBlueOrange.id)
            else -> binding.bgStyleGroup.check(binding.radioBgDefault.id)
        }
    }

    private fun setupHandlers() {
        val prefs = getSharedPreferences("musep50_prefs", MODE_PRIVATE)

        binding.switchDarkMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            prefs.edit().putBoolean("pref_dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("pref_notifications", isChecked).apply()
        }

        binding.switchEdgeToEdge.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("pref_edge_to_edge", isChecked).apply()
            applyEdgeToEdgeFromPrefs()
        }

        binding.bgStyleGroup.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                binding.radioBgOrangeWhite.id -> "orange_white"
                binding.radioBgBlueOrange.id -> "blue_orange"
                else -> "default"
            }
            prefs.edit().putString("pref_bg_style", value).apply()
            applyBackgroundFromPrefs()
        }

        binding.btnResetExportDir.setOnClickListener {
            val path = defaultExportPath()
            prefs.edit().putString("pref_export_dir", path).apply()
            binding.exportPath.text = path
        }

        binding.btnClearCache.setOnClickListener {
            try {
                cacheDir.deleteRecursively()
                externalCacheDir?.deleteRecursively()
                binding.cacheStatus.text = "Cache vidé"
            } catch (_: Exception) {
                binding.cacheStatus.text = "Erreur pendant le nettoyage"
            }
        }
    }

    private fun defaultExportPath(): String {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val musep = File(downloads, "Musep")
        return musep.absolutePath
    }
}
