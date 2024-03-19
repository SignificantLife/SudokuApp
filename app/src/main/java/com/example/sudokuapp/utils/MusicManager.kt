package com.example.sudokuapp.utils

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.service.MusicService

object MusicManager {

    private var mMusicService: MusicService? = null
    private var mIsBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            mMusicService = binder.getService()
            mIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIsBound = false
        }
    }

    fun init() {
        bindMusicService()
    }

    private fun bindMusicService() {
        val intent = Intent(SudokuApplication.context, MusicService::class.java)
        SudokuApplication.context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun pauseMusic() {
        mMusicService?.pauseMusic()
    }

    fun resumeMusic() {
        mMusicService?.resumeMusic()
    }

    fun stopMusic() {
        mMusicService?.stopMusic()
    }


    fun release() {
        if (mIsBound) {
            mMusicService?.release()
            SudokuApplication.context.unbindService(connection)
            mIsBound = false
        }
    }
}




