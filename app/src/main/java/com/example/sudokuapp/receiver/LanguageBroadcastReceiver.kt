package com.example.sudokuapp.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import android.window.SplashScreen
import androidx.core.content.ContextCompat
import com.example.sudokuapp.R
import com.example.sudokuapp.activities.SplashActivity
import java.util.Locale

class LanguageBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = context?.let { ContextCompat.getString(it, R.string.language_receiver) }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}