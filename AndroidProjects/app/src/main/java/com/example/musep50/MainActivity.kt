package com.example.musep50

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.ui.DashboardActivity
import com.example.musep50.ui.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPreferences = getSharedPreferences("musep50_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("current_user_id", -1L)
        
        if (userId != -1L) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        finish()
    }
}