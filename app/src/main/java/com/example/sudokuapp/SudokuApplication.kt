package com.example.sudokuapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.sudokuapp.service.MusicService
import com.example.sudokuapp.utils.MusicManager
import com.example.sudokuapp.utils.SharedPreferencesManager

class SudokuApplication: Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        SharedPreferencesManager.init(context)


//        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
//
//        if(musicSwitch){
//            MusicManager.init()
//            Log.d("data","music init")
//        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }


    override fun onTerminate() {
        super.onTerminate()
    }

}