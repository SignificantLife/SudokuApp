package com.example.sudokuapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sudokuapp.R

class FirstRuleLayout(context: Context, attrs: AttributeSet):ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.how_to_play_item_1, this)
    }
}