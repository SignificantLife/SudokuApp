package com.example.sudokuapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sudokuapp.R
import com.example.sudokuapp.adapter.GameLevelAdapter
import com.example.sudokuapp.data.DialogType
import com.example.sudokuapp.utils.ConfigUtils
import com.example.sudokuapp.data.GameLevel
import com.example.sudokuapp.data.LevelItemLayout
import com.example.sudokuapp.data.RealGameLevel
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.databinding.ActivityLevelSelectionBinding
import com.example.sudokuapp.fragments.GameDialogFragment
import com.example.sudokuapp.fragments.SettingDialogFragment
import com.example.sudokuapp.utils.ActivityCollector
import com.example.sudokuapp.utils.SharedPreferencesManager
import com.example.sudokuapp.utils.SoundManager
import com.example.sudokuapp.views.BounceEdgeEffectFactory

class LevelSelectionActivity : BaseActivity(), GameLevelAdapter.ClickListener, GameDialogFragment.DialogListener {

    private lateinit var binding: ActivityLevelSelectionBinding

    private var mSmallMeteorWidth: Float = 0f
    private var mSmallMeteorHeight: Float = 0f
    private var mBigMeteorWidth: Float = 0f
    private var mBigMeteorHeight: Float = 0f

    private val mBigMeteorHandler = Handler(Looper.getMainLooper())
    private val mSmallMeteorHandler = Handler(Looper.getMainLooper())
    private val mBigMeteorDelay = 6000L

    private val mLevelList = ArrayList<GameLevel>()
    private var mAdapter: GameLevelAdapter?=null

    private var mGameLevels: Int = 0
    private var mNextLevel: Int = 0

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectionBinding.inflate(layoutInflater)
        val view = binding.root

        binding.smallMeteorView.post {
            mSmallMeteorWidth = binding.smallMeteorView.width.toFloat()
            mSmallMeteorHeight = binding.smallMeteorView.height.toFloat()
        }

        binding.bigMeteorView.post {
            mBigMeteorWidth = binding.bigMeteorView.width.toFloat()
            mBigMeteorHeight = binding.bigMeteorView.height.toFloat()
        }

        setContentView(view)


        /* RecyclerView Setting */
        val isGameWon = intent.getBooleanExtra("game_result",false)
        mGameLevels = ConfigUtils.getIntValue(this, "game_levels", 0)
        mNextLevel = SharedPreferencesManager.getNextLevel()

        initGameLevel()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        binding.recyclerView.layoutManager = layoutManager
        mAdapter = GameLevelAdapter(mLevelList, mGameLevels, isGameWon)
        mAdapter!!.setListener(this)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.edgeEffectFactory = BounceEdgeEffectFactory(this)



        /* Open Setting DialogFragment */
        binding.settingImg.setOnClickListener {

            SoundManager.playSound(SoundType.BUTTON_TAP)

            val dialogFragment = SettingDialogFragment()
            dialogFragment.show(supportFragmentManager, "setting_dialog")

            SoundManager.playSound(SoundType.POPUP_APPEAR)
        }


        /* Return Back - Exit App */
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ActivityCollector.finishAll()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initGameLevel(){

        /* Add Bottom Space */
        val bottom =LevelItemLayout(GameLevel.BOTTOM_SPACE)
        mLevelList.add(bottom)

        /* Add Level Item */
        var level: GameLevel
        var type: Int
        var isSmallPattern: Boolean

        for (i in 1..mGameLevels){
            var isCompleted = false
            if(i<mNextLevel) isCompleted = true
            isSmallPattern = i == 1 || (i-1) % 5 == 0
            type  = checkPosition(i, isSmallPattern)

            level = RealGameLevel(type, i, isCompleted, isSmallPattern)
            mLevelList.add(level)
        }

        /* Add Top Text - The Last Item */
        val top = LevelItemLayout(GameLevel.TOP_TEXT)
        mLevelList.add(top)

    }

    private fun checkPosition(level: Int, isSmallPattern: Boolean): Int {
        val isOddLevel = level % 2 != 0
        val isMultipleOf4 = level % 4 == 0

        return when {
            isOddLevel -> {
                if (isSmallPattern) {
                    if ((level + 1) % 4 == 0) GameLevel.SMALL_MID_RIGHT
                    else GameLevel.SMALL_MID_LEFT
                } else {
                    if ((level + 1) % 4 == 0) GameLevel.NORMAL_MID_RIGHT
                    else GameLevel.NORMAL_MID_LEFT
                }
            }
            isMultipleOf4 -> {
                if (isSmallPattern) GameLevel.SMALL_RIGHT
                else GameLevel.NORMAL_RIGHT
            }
            else -> {
                if (isSmallPattern) GameLevel.SMALL_LEFT
                else GameLevel.NORMAL_LEFT
            }
        }
    }


    private val runnable = object : Runnable {
        var isFirstTime = true

        override fun run() {
            if (isFirstTime) {
                playBigMeteorAnimation()
                isFirstTime = false
                mSmallMeteorHandler.postDelayed(this, 2000L)
            }
            else {
                playSmallMeteorAnimation()
                isFirstTime = true
                mBigMeteorHandler.postDelayed(this, 4000L)
            }
        }
    }


    override fun onResume() {
        super.onResume()

        /* Night Sky Animation */
        val nightSkyAnimation: Animation = AnimationUtils.loadAnimation(binding.starView.context, R.anim.night_sky_flash)
        binding.starView.startAnimation(nightSkyAnimation)

        mBigMeteorHandler.postDelayed(runnable, mBigMeteorDelay)
    }


    private fun playSmallMeteorAnimation(){
        binding.smallMeteorView.visibility=View.VISIBLE
        val smallMeteor = binding.smallMeteorView
        val alphaAnimation = ObjectAnimator.ofFloat(smallMeteor,"alpha",0f,1f)
        alphaAnimation.startDelay = 0
        alphaAnimation.duration = 200
        alphaAnimation.repeatCount = ValueAnimator.INFINITE
        alphaAnimation.repeatMode=ValueAnimator.REVERSE

        alphaAnimation.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                val reverseAlphaAnimation = ObjectAnimator.ofFloat(smallMeteor, "alpha", 1f, 0f)
                reverseAlphaAnimation.startDelay = 400
                reverseAlphaAnimation.duration = 200
                reverseAlphaAnimation.start()
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimation, smallMeteorTranslateAnimation(smallMeteor))

        animatorSet.start()
    }

    private fun smallMeteorTranslateAnimation(view: View): AnimatorSet {
        val smallMeteor = binding.smallMeteorView
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val translationX = ObjectAnimator.ofFloat(view, "translationX", -screenWidth/2-10f)
        val translationY = ObjectAnimator.ofFloat(view, "translationY", mSmallMeteorHeight*2)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationX, translationY)
        animatorSet.duration = 1200

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                smallMeteor.translationX = 0f
                smallMeteor.translationY = 0f
                binding.smallMeteorView.visibility=View.INVISIBLE
            }
        })
        return animatorSet
    }

    private fun playBigMeteorAnimation() {
        val bigMeteor = binding.bigMeteorView
        val alphaAnimation = ObjectAnimator.ofFloat(bigMeteor,"alpha",0f,1f)
        alphaAnimation.startDelay = 0
        alphaAnimation.duration = 200
        alphaAnimation.repeatCount = ValueAnimator.INFINITE
        alphaAnimation.repeatMode=ValueAnimator.REVERSE

        alphaAnimation.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                val reverseAlphaAnimation = ObjectAnimator.ofFloat(bigMeteor, "alpha", 1f, 0f)
                reverseAlphaAnimation.startDelay = 1000
                reverseAlphaAnimation.duration = 200
                reverseAlphaAnimation.start()
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimation, bigMeteorTranslateAnimation(bigMeteor))

        animatorSet.start()
    }

    private fun bigMeteorTranslateAnimation(view: View): AnimatorSet {
        val bigMeteor = binding.bigMeteorView
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val translationX = ObjectAnimator.ofFloat(view, "translationX", -screenWidth)
        val translationY = ObjectAnimator.ofFloat(view, "translationY", mBigMeteorHeight*2)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationX, translationY)
        animatorSet.duration = 1200

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                bigMeteor.translationX = 0f
                bigMeteor.translationY = 0f
            }
        })

        return animatorSet
    }

    override fun onPause() {
        super.onPause()

        binding.starView.clearAnimation()

        mBigMeteorHandler.removeCallbacks(runnable)
        mSmallMeteorHandler.removeCallbacks(runnable)
    }

    override fun onGameLevelClicked(level: Int, isWon: Boolean) {

        SoundManager.playSound(SoundType.BUTTON_TAP)

        val dialogFragment = GameDialogFragment(DialogType.START_GAME, level, isWon)
        dialogFragment.show(supportFragmentManager, "game_start_dialog")
        SoundManager.playSound(SoundType.POPUP_APPEAR)
    }

    override fun onWonGame(data: Boolean) {
        /* Do nothing */
    }
}
