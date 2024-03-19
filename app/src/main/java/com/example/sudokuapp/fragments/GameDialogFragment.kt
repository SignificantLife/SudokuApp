package com.example.sudokuapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.activities.GamePlayActivity
import com.example.sudokuapp.activities.LevelSelectionActivity
import com.example.sudokuapp.data.DialogType
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.FragmentGameDialogBinding
import com.example.sudokuapp.utils.SoundManager

class GameDialogFragment(private val dialogType: DialogType?, private val level: Int?, private val isWon: Boolean?): androidx.fragment.app.DialogFragment() {
    private lateinit var binding: FragmentGameDialogBinding
    private var mLevel: Int? = level
    private val mStarOnBitmap = R.drawable.ic_pop_star
    private val mStarOffBitmap = R.drawable.ic_pop_star_bg
    private var mBtnEvent: (() -> Unit)? = null
    private var mListener: DialogListener? = null


    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        binding = FragmentGameDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(binding.root)


        if(isWon == true){
            binding.wonStarImg.visibility = View.VISIBLE
        }


        /* Check Dialog Type */
        val levelTxtPrefix = ContextCompat.getString(requireContext(), R.string.level_text_prefix)

        if(dialogType == DialogType.START_GAME){
            binding.titleTxt.text = "$levelTxtPrefix$mLevel"
            binding.btnTxt.text = ContextCompat.getString(requireContext(), R.string.game_start_btn_txt)
            mBtnEvent = { startGame() }
        }
        else if(dialogType == DialogType.WON_GAME){
            binding.starImg.visibility = View.GONE
            binding.titleTxt.text = ContextCompat.getString(requireContext(), R.string.game_won_txt)
            binding.btnTxt.text = ContextCompat.getString(requireContext(), R.string.game_won_btn_txt)
            mBtnEvent = { wonGame() }

            /* Won Game Animation */
            val wonGameAnimation: Animation = AnimationUtils.loadAnimation(binding.wonStarImg.context, R.anim.won_effect)
            binding.wonStarImg.startAnimation(wonGameAnimation)

            val handler = Handler(Looper.getMainLooper())

            handler.postDelayed({
                SoundManager.playSound(SoundType.POPUP_STAR)
            }, 500)


        }
        else if(dialogType == DialogType.LOST_GAME){
            binding.titleTxt.text = ContextCompat.getString(requireContext(), R.string.game_lost_txt)
            binding.btnTxt.text = ContextCompat.getString(requireContext(), R.string.game_lost_btn_txt)
            mBtnEvent = { lostGame() }
        }


        binding.btnTxt.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            dismiss()
            mBtnEvent?.invoke()
        }

        binding.closeView.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            if(dialogType == DialogType.WON_GAME || dialogType == DialogType.LOST_GAME){
                dismiss()
                val intent = Intent(requireContext(), LevelSelectionActivity::class.java)

                val isGameWon = dialogType == DialogType.WON_GAME

                intent.putExtra("game_result", isGameWon)
                startActivity(intent)
            }
            else{
                dismiss()
            }

        }

        this.isCancelable = false
        return dialogBuilder.create()
    }


    private fun startGame(){
        val intent = Intent(activity, GamePlayActivity::class.java)
        intent.putExtra("game_level", level)
        startActivity(intent)

        /* start new activity with animation, exit current activity without animation */
        activity?.overridePendingTransition(R.anim.game_enter,0)
    }

    private fun wonGame(){
        mListener?.onWonGame(true)
    }

    private fun lostGame(){
        mListener?.onWonGame(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    interface DialogListener {
        fun onWonGame(data: Boolean)
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