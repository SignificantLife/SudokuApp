package com.example.sudokuapp.utils

import android.content.Context
import android.media.SoundPool
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.service.MusicService
import java.util.EnumMap


object SoundManager {
    private var mSoundPool: SoundPool = SoundPool.Builder().setMaxStreams(2).build()
    private var mSoundMap: EnumMap<SoundType, Int> = EnumMap(SoundType::class.java)

    init {
        mSoundMap[SoundType.BUTTON_TAP] = mSoundPool.load(SudokuApplication.context, R.raw.button_tap, 1)
        mSoundMap[SoundType.MAP_STAR_ON] = mSoundPool.load(SudokuApplication.context, R.raw.map_star_on, 1)
        mSoundMap[SoundType.POPUP_APPEAR] = mSoundPool.load(SudokuApplication.context,R.raw.popup_appear,1)
        mSoundMap[SoundType.POPUP_STAR] = mSoundPool.load(SudokuApplication.context,R.raw.popup_star,1)
        mSoundMap[SoundType.PUZZLE_COMPLETE] = mSoundPool.load(SudokuApplication.context, R.raw.puzzle_complete,1)
        mSoundMap[SoundType.PUZZLE_FAIL] = mSoundPool.load(SudokuApplication.context, R.raw.puzzle_fail,1)
        mSoundMap[SoundType.TIME] = mSoundPool.load(SudokuApplication.context,R.raw.time,1)
        mSoundMap[SoundType.WRONG_PLACEMENT] = mSoundPool.load(SudokuApplication.context, R.raw.wrong_placement,1)
    }

    fun playSound(soundId: SoundType) {
        val soundSwitch = SharedPreferencesManager.getSoundSwitch()

        if(soundSwitch){
            val id = mSoundMap[soundId] ?: return
            mSoundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f)
        }

    }
}