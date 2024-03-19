package com.example.sudokuapp.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.sudokuapp.R
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.ActivityGameRulesBinding
import com.example.sudokuapp.utils.SoundManager

class GameRulesActivity : BaseActivity() {

    private lateinit var binding: ActivityGameRulesBinding

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRulesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val titleTxt = ContextCompat.getString(this, R.string.game_rules_title)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)

            /*ActionBar Layout*/
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
            title = SpannableString(titleTxt).apply {
                setSpan(ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                SoundManager.playSound(SoundType.BUTTON_TAP)

                onBackPressedDispatcher.onBackPressed()

                /*'overridePendingTransition is deprecated, use overrideActivityTransition*/
                overridePendingTransition(R.anim.level_enter, R.anim.rule_exit);
                //overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_enter, R.anim.slide_exit, Color.TRANSPARENT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
