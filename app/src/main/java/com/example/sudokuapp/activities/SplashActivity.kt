package com.example.sudokuapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.databinding.ActivitySplashBinding
import com.example.sudokuapp.service.MusicService
import com.example.sudokuapp.utils.MusicManager
import com.example.sudokuapp.utils.SoundManager

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        displayAnimation()

        binding.frameLayout.setOnClickListener {
            val intent = Intent(SudokuApplication.context, LevelSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayAnimation(){
        val skyView = binding.frameLayout
        val alphaAnimation = ObjectAnimator.ofFloat(skyView, "alpha", 0.5f, 1f)
        alphaAnimation.duration = 1500
        alphaAnimation.repeatCount = ValueAnimator.INFINITE
        alphaAnimation.repeatMode = ValueAnimator.REVERSE

        alphaAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val reverseAlphaAnimation = ObjectAnimator.ofFloat(skyView, "alpha", 1f, 0.5f)
                reverseAlphaAnimation.duration = 1500
                reverseAlphaAnimation.start()
            }
        })

        alphaAnimation.start()

    }


}
