package com.example.musep50.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musep50.R

class OperationDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation_details)
        
        val operationId = intent.getLongExtra("operation_id", -1L)
    }
}
