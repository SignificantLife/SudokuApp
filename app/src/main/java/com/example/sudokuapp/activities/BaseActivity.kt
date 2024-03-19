package com.example.sudokuapp.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sudokuapp.utils.ActivityCollector
import com.example.sudokuapp.utils.MusicManager

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    override fun onStart() {
        super.onStart()
        ActivityCollector.addVisibleActivity()
    }

    override fun onStop() {
        super.onStop()
        ActivityCollector.removeVisibleActivity()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }



}