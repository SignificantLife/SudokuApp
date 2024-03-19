package com.example.sudokuapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.utils.MusicManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class MusicService : Service(), CoroutineScope by MainScope() {

    private lateinit var mMediaPlayer: MediaPlayer
    private val mBinder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mMediaPlayer = MediaPlayer.create(this, R.raw.bgm)
        mMediaPlayer.isLooping = true
        mMediaPlayer.setOnPreparedListener {
            mMediaPlayer.start()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        cancel()
    }


    fun pauseMusic() {
        launch {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
            }
        }
    }


    fun resumeMusic() {
        launch {
            if (mMediaPlayer!=null &&!mMediaPlayer.isPlaying) {
                mMediaPlayer.start()
            }
        }
    }


    fun stopMusic() {
        launch {
            if (mMediaPlayer!=null &&mMediaPlayer.isPlaying) {
                mMediaPlayer.stop()
            }
        }
    }

    fun release(){
        launch {
            if(mMediaPlayer!=null &&mMediaPlayer.isPlaying){
                mMediaPlayer.release()
            }
        }
    }
}
