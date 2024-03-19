package com.example.sudokuapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.sudokuapp.R
import com.example.sudokuapp.activities.GameRulesActivity
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.FragmentSettingDialogBinding
import com.example.sudokuapp.utils.MusicManager
import com.example.sudokuapp.utils.SharedPreferencesManager
import com.example.sudokuapp.utils.SoundManager

class SettingDialogFragment: androidx.fragment.app.DialogFragment() {

    private lateinit var binding: FragmentSettingDialogBinding

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = FragmentSettingDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(binding.root)

        val musicSwitch = SharedPreferencesManager.getMusicSwitch()
        binding.musicBtn.isChecked = musicSwitch

        val soundSwitch = SharedPreferencesManager.getSoundSwitch()
        binding.soundBtn.isChecked = soundSwitch

        binding.gameRuleTxt.setOnClickListener {

            dismiss()

            SoundManager.playSound(SoundType.BUTTON_TAP)

            val intent = Intent(activity, GameRulesActivity::class.java)
            startActivity(intent)

            /*'overridePendingTransition is deprecated, use overrideActivityTransition*/
            //activity?.overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_enter, R.anim.slide_exit, Color.TRANSPARENT)

            /* */
            activity?.overridePendingTransition(R.anim.rule_enter,R.anim.level_exit)
        }

        binding.closeView.setOnClickListener {
            SoundManager.playSound(SoundType.BUTTON_TAP)
            dismiss()
        }
        
        binding.musicBtn.setOnCheckedChangeListener { buttonView, isChecked ->

            SoundManager.playSound(SoundType.BUTTON_TAP)

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

            SoundManager.playSound(SoundType.BUTTON_TAP)

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
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}