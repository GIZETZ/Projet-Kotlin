
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
        
        // Toujours afficher l'écran d'intro avec vidéo à chaque lancement
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
