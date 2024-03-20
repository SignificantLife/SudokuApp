package com.example.sudokuapp.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.sudokuapp.R
import com.example.sudokuapp.data.DialogType
import com.example.sudokuapp.views.NineGridView
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.ActivityGamePlayBinding
import com.example.sudokuapp.fragments.GameDialogFragment
import com.example.sudokuapp.fragments.PauseDialogFragment
import com.example.sudokuapp.utils.ConfigUtils
import com.example.sudokuapp.utils.SharedPreferencesManager
import com.example.sudokuapp.utils.SoundManager
import java.util.LinkedList
import java.util.Queue

class GamePlayActivity : BaseActivity(), PauseDialogFragment.DialogListener, GameDialogFragment.DialogListener, NineGridView.NineGridViewListener {

    private lateinit var binding: ActivityGamePlayBinding

    private var mLevel: Int = 0
    private var mLevelTxtPrefix: String = ""
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var mTimeLeftInMillis: Long = 0
    private var mPauseTimer: Boolean = false
    private var mGameTime: Int = 0
    private var mIsBackground: Boolean = false


    private var mGuideImg: Queue<Int> = LinkedList<Int>().apply {
        //add(R.drawable.guide_cover1)
        add(R.drawable.guide_cover2)
        add(R.drawable.guide_cover3)
        add(R.drawable.guide_cover4)
        add(R.drawable.guide_cover5)
        add(R.drawable.guide_cover6)
    }

    private var mGuideTxt: Queue<Int> = LinkedList<Int>().apply {
        //add(R.string.guide_text_1)
        add(R.string.guide_text_2)
        add(R.string.guide_text_3)
        add(R.string.guide_text_4)
        add(R.string.guide_text_5)
        add(R.string.guide_text_6)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamePlayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /* Layout Text Setting */
        mGameTime = ConfigUtils.getIntValue(this,"game_time_minute",10)
        mLevel = intent.getIntExtra("game_level",1)
        mLevelTxtPrefix = ContextCompat.getString(this, R.string.level_text_prefix)
        binding.levelTxt.text = mLevelTxtPrefix + mLevel.toString()
        binding.timerTxt.text = "$mGameTime:00"


        NineGridView.setBoardData(binding.nineGridView,mLevel)
        binding.nineGridView.setNineGridViewListener(this)

        val pauseDialogFragment = PauseDialogFragment(false)
        pauseDialogFragment.setDialogListener(this)

        val gameDialogFragment = GameDialogFragment(null,null,null)
        gameDialogFragment.setDialogListener(this)

        binding.guideLayout.setOnClickListener {
            SoundManager.playSound(SoundType.BUTTON_TAP)
            if(mGuideImg.isEmpty() || mGuideImg.isEmpty()){
                binding.guideLayout.visibility= View.GONE
                setTimer()
            }
            else {
                guidePlay()
            }
        }


        /* Open Pause DialogFragment */
        binding.pauseImg.setOnClickListener {
            SoundManager.playSound(SoundType.BUTTON_TAP)
            pauseEvent()
        }


        /* Return Back - Invalid */
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                /* do nothing */
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)



    }


    private fun guidePlay(){
        val drawableId = mGuideImg.poll()
        val textId = mGuideTxt.poll()

        binding.guideLayout.background = drawableId?.let { ContextCompat.getDrawable(this, it) }
        binding.guideTxt.text = textId?.let { ContextCompat.getString(this, it) }
    }


    private fun setTimer() {
        val totalTime = mGameTime * 60000
        binding.progressbar.max = totalTime / 1000
        binding.progressbar.progress = binding.progressbar.max
        mTimeLeftInMillis = totalTime.toLong()

        mHandler = Handler(Looper.getMainLooper())
        mRunnable = object : Runnable {
            override fun run() {
                if (mTimeLeftInMillis > 0) {
                    if (!mPauseTimer) {
                        mTimeLeftInMillis -= 1000

                        binding.progressbar.progress = (mTimeLeftInMillis / 1000).toInt()

                        val minutes = (mTimeLeftInMillis / 1000) / 60
                        val seconds = (mTimeLeftInMillis / 1000) % 60
                        val timeLeft = String.format("%02d:%02d", minutes, seconds)
                        binding.timerTxt.text = timeLeft

                        mHandler.postDelayed(this, 1000)

                        if(mTimeLeftInMillis <= 10*1000){
                            countDownSound()
                        }

                    }
                } else {
                    /* Time is over */
                    val dialogFragment = GameDialogFragment(DialogType.LOST_GAME, mLevel, false)
                    dialogFragment.show(supportFragmentManager, "lost_dialog")
                    SoundManager.playSound(SoundType.PUZZLE_FAIL)
                }
            }
        }
        mHandler.post(mRunnable)
    }

    private fun pauseTimer() {
        mPauseTimer = true
        mHandler.removeCallbacks(mRunnable)
    }

    private fun resumeTimer() {
        mHandler.postDelayed(mRunnable, 1000)
    }

    private fun resetTimer() {
        mHandler.removeCallbacks(mRunnable)
        mPauseTimer = false
        setTimer()
    }


    override fun onDataPassed(data: Boolean) {
        if(data){
            NineGridView.setBoardData(binding.nineGridView, mLevel)
            resetTimer()
        }
    }

    override fun onClosed(data: Boolean) {
        if(data){
            resumeTimer()
            mPauseTimer = false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onWonGame(data: Boolean) {
        if(data){
            /* Next Level NineGridView*/
            mLevel += 1
            NineGridView.setBoardData(binding.nineGridView,mLevel)
            binding.levelTxt.text = mLevelTxtPrefix + mLevel.toString()
            resetTimer()
        }
        else{
            /* Try Again */
            NineGridView.setBoardData(binding.nineGridView, mLevel)
            resetTimer()
        }
    }

    override fun onIsWon(data: Boolean) {
        if(data) {

            pauseTimer()
            mPauseTimer = true

            SoundManager.playSound(SoundType.POPUP_APPEAR)
            SoundManager.playSound(SoundType.PUZZLE_COMPLETE)

            val handler = Handler(Looper.getMainLooper())

            handler.postDelayed({
                SharedPreferencesManager.putNextLevel(mLevel+1)
                val dialogFragment = GameDialogFragment(DialogType.WON_GAME, mLevel, true)
                dialogFragment.show(supportFragmentManager, "won_dialog")
            }, 2000)

        }
    }

    private fun countDownSound(){
        SoundManager.playSound(SoundType.TIME)
    }

    private fun pauseEvent(){
        pauseTimer()
        mPauseTimer = true

        val dialogFragment = PauseDialogFragment(false)
        dialogFragment.show(supportFragmentManager, "pause_dialog")
        SoundManager.playSound(SoundType.POPUP_APPEAR)
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
        mPauseTimer = true
        mIsBackground = true
    }

    override fun onResume() {
        super.onResume()
        if (mIsBackground) {
            val existingDialog = supportFragmentManager.findFragmentByTag("pause_dialog") as? PauseDialogFragment
            if (existingDialog == null) {
                val dialogFragment = PauseDialogFragment(mIsBackground)
                dialogFragment.show(supportFragmentManager, "pause_dialog")
                SoundManager.playSound(SoundType.POPUP_APPEAR)

                /* Fast Loading */
                supportFragmentManager.executePendingTransactions()
            }
        }
    }
}

