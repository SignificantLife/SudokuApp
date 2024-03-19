package com.example.sudokuapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {

    private const val PREF_NAME = "sudoku_app_prefs"
    private lateinit var mSharedPreferences: SharedPreferences

    /* Game Setting */
    private const val NEXT_LEVEL = "next_level"

    /* Music Setting */
    private const val MUSIC_SWITCH = "music_switch"

    /* Sound Setting */
    private const val SOUND_SWITCH = "sound_switch"

    fun init(context: Context) {
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getNextLevel(): Int {
        return mSharedPreferences.getInt(NEXT_LEVEL, 1)
    }

    @SuppressLint("CommitPrefEdits")
    fun putNextLevel(level: Int) {
        val editor = mSharedPreferences.edit()
        editor.putInt(NEXT_LEVEL, level)
        editor.apply()
    }

    fun getMusicSwitch(): Boolean {
        return mSharedPreferences.getBoolean(MUSIC_SWITCH, true)
    }

    fun putMusicSwitch(value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(MUSIC_SWITCH, value)
        editor.apply()
    }

    fun getSoundSwitch(): Boolean {
        return mSharedPreferences.getBoolean(SOUND_SWITCH, true)
    }

    fun putSoundSwitch(value: Boolean){
        val editor = mSharedPreferences.edit()
        editor.putBoolean(SOUND_SWITCH, value)
        editor.apply()
    }
}