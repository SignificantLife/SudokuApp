package com.example.sudokuapp.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.example.sudokuapp.R
import com.example.sudokuapp.activities.GamePlayActivity
import com.example.sudokuapp.activities.LevelSelectionActivity
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.FragmentPauseDialogBinding
import com.example.sudokuapp.databinding.FragmentSettingDialogBinding
import com.example.sudokuapp.utils.MusicManager
import com.example.sudokuapp.utils.SharedPreferencesManager
import com.example.sudokuapp.utils.SoundManager

class PauseDialogFragment(val isBackground: Boolean): androidx.fragment.app.DialogFragment() {

    private lateinit var binding:FragmentPauseDialogBinding
    private var mListener: DialogListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = FragmentPauseDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(binding.root)

        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
        binding.musicBtn.isChecked = musicSwitch

        val soundSwitch = SharedPreferencesManager.getSoundSwitch()
        binding.soundBtn.isChecked = soundSwitch

        binding.closeView.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            mListener?.onClosed(true)
            dismiss()
        }

        binding.restartTxt.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            mListener?.onDataPassed(true)
            dismiss()
        }

        binding.gameLevelTxt.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            val intent = Intent(activity, LevelSelectionActivity::class.java)
            startActivity(intent)

            /* start new activity without animation, exit current activity with animation */
            activity?.overridePendingTransition(0,R.anim.game_exit)
        }

        binding.musicBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SharedPreferencesManager.putMusicSwitch(true)
                MusicManager.init()
            } else {
                SharedPreferencesManager.putMusicSwitch(false)
                MusicManager.stopMusic()
                MusicManager.release()
            }
        }

        binding.soundBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                SharedPreferencesManager.putSoundSwitch(true)
            } else {
                SharedPreferencesManager.putSoundSwitch(false)
            }
        }

        this.isCancelable = false
        return dialogBuilder.create()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!isBackground) {
            dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    interface DialogListener {
        fun onDataPassed(data: Boolean)
        fun onClosed(data: Boolean)
    }

    fun setDialogListener(listener: DialogListener) {
        this.mListener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DialogListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement DialogListener")
        }
    }
}