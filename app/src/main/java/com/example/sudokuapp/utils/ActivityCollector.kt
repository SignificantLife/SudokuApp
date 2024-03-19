package com.example.sudokuapp.utils

import android.app.Activity
import android.util.Log

object ActivityCollector {

    private val mActivities = ArrayList<Activity>()
    private var mVisibleActivityCount = 0

    fun addActivity(activity: Activity){

        val musicSwitch = SharedPreferencesManager.getMusicSwitch()

        if(mActivities.size == 0 && musicSwitch){
            MusicManager.init()
        }

        mActivities.add(activity)
    }

    fun removeActivity(activity: Activity){
        mActivities.remove(activity)
    }

    fun finishAll(){

        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
        if(musicSwitch){
            MusicManager.stopMusic()
            MusicManager.release()
        }

        for (activity in mActivities){
            if(!activity.isFinishing){
                activity.finish()
            }
        }
        mActivities.clear()

    }


    fun addVisibleActivity(){
        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
        if(mVisibleActivityCount == 0 && musicSwitch){
            MusicManager.resumeMusic()
        }

        mVisibleActivityCount++
    }

    fun removeVisibleActivity(){
        mVisibleActivityCount--
        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
        if(mVisibleActivityCount == 0 && musicSwitch ){
            MusicManager.pauseMusic()
        }
    }



}