package com.example.sudokuapp.data

import android.widget.ImageView

sealed class GameLevel(val type: Int) {
    companion object {
        const val BOTTOM_SPACE = -2
        const val TOP_TEXT = -1
        const val SMALL_MID_LEFT = 0
        const val SMALL_MID_RIGHT = 1
        const val SMALL_LEFT = 2
        const val SMALL_RIGHT = 3
        const val NORMAL_MID_LEFT = 4
        const val NORMAL_MID_RIGHT = 5
        const val NORMAL_LEFT = 6
        const val NORMAL_RIGHT = 7
    }
}

class RealGameLevel(type: Int, val level: Int, val isCompleted: Boolean, val isSmallPattern: Boolean) : GameLevel(type)
class LevelItemLayout(type: Int) : GameLevel(type)
