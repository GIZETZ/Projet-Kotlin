
package com.example.musep50

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.databinding.ActivityMainBinding
import com.example.musep50.ui.DashboardActivity
import com.example.musep50.ui.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("current_user_id", -1L)
        
        // Si l'utilisateur est déjà connecté, aller directement au Dashboard
        if (userId != -1L) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }
        
        // Sinon, afficher l'écran d'intro avec vidéo
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupVideoView()
        setupContinueButton()
    }
    
    private fun setupVideoView() {
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.deo)
        binding.videoView.setVideoURI(videoUri)
        
        // Lecture automatique en boucle
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }
        
        // Démarrer la vidéo
        binding.videoView.start()
    }
    
    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            binding.videoView.start()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (::binding.isInitialized) {
            binding.videoView.pause()
        }
    }
}
