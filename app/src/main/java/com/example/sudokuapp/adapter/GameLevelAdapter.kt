package com.example.sudokuapp.adapter

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.SudokuApplication.Companion.context
import com.example.sudokuapp.data.GameLevel
import com.example.sudokuapp.data.RealGameLevel
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.utils.ConfigUtils
import com.example.sudokuapp.utils.SharedPreferencesManager
import com.example.sudokuapp.utils.SoundManager

class GameLevelAdapter(private val levelList:List<GameLevel>, val gameLevels:Int, val isGameWon: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var mListener: ClickListener? = null
    private var mNextLevel = SharedPreferencesManager.getNextLevel()
    private var mLightBgView: ImageView? = null
    private var mLightStarView: ImageView? = null

    abstract class BaseLevelViewHolder(view: View, val gameLevels: Int, var isGameWon: Boolean) : RecyclerView.ViewHolder(view) {
        private val level: TextView = view.findViewById(R.id.level)
        private val levelView: ImageView = view.findViewById(R.id.levelView)
        private val line: ImageView = view.findViewById(R.id.line)
        private val lightBg: ImageView = view.findViewById(R.id.lightBg)
        private val wonLevelColor = ContextCompat.getColor(context, R.color.yellow)
        private val normalLevelColor = ContextCompat.getColor(context, R.color.white)
        private var nextLevel = SharedPreferencesManager.getNextLevel()


        fun bindLevelData(levelData: RealGameLevel) {
            level.text = levelData.level.toString()


            if (levelData.isCompleted && levelData.level < nextLevel) {

                level.setTextColor(wonLevelColor)
                setStarAndLineImage(true, levelData.level)

                if(isGameWon && levelData.level == nextLevel - 1){
                    val lightStarAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.light_star)
                    levelView.startAnimation(lightStarAnimation)
                    SoundManager.playSound(SoundType.MAP_STAR_ON)
                    isGameWon = false
                }

            } else {
                level.setTextColor(normalLevelColor)
                setStarAndLineImage(false, levelData.level)
            }

            if (levelData.level == gameLevels) {
                line.visibility = View.GONE
            }
            else{
                line.visibility = View.VISIBLE
            }


            if(levelData.level == nextLevel){
                setStarAndLineImage(false, levelData.level)
                val flashAnimation: Animation = AnimationUtils.loadAnimation(levelView.context, R.anim.star_flash)
                lightBg.startAnimation(flashAnimation)
            }
            else{
                lightBg.clearAnimation()
            }
        }

        private fun setStarAndLineImage(isWon: Boolean, level: Int) {
            /* Star Image */

            val starImageRes = if (levelView.tag == "small") {
                if(isWon) R.drawable.ic_map_star_small_on else R.drawable.ic_map_star_small_off
            }else{
                if(isWon) R.drawable.ic_map_star_normal_on else R.drawable.ic_map_star_normal_off
            }


            /* Line Image */
            val lineImageRes = if (isWon) {
                if (nextLevel > 1 && level + 1 != nextLevel) {
                    if (line.tag == "left") R.drawable.ic_map_line_left_on else R.drawable.ic_map_line_right_on
                } else {
                    if (line.tag == "left") R.drawable.ic_map_line_left_off else R.drawable.ic_map_line_right_off
                }
            } else {
                if (line.tag == "left") R.drawable.ic_map_line_left_off else R.drawable.ic_map_line_right_off
            }

            levelView.setImageResource(starImageRes)
            line.setImageResource(lineImageRes)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val level = levelList[position]
        return level.type
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = viewType
        val nextLevel = SharedPreferencesManager.getNextLevel()
        val effectiveLevels = ConfigUtils.getIntValue(context,"effective_levels",10)

        return when (viewType) {
            GameLevel.BOTTOM_SPACE -> object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_bottom_space, parent, false)
            ){}
            GameLevel.TOP_TEXT -> object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_top_text, parent, false)
            ){}
            GameLevel.SMALL_MID_LEFT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_small_mid_left, parent,false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.SMALL_MID_RIGHT -> object : BaseLevelViewHolder(
                 LayoutInflater.from(parent.context).inflate(R.layout.level_small_mid_right, parent,false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.SMALL_LEFT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_small_left, parent, false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.SMALL_RIGHT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_small_right, parent, false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.NORMAL_MID_LEFT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_normal_mid_left, parent,false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.NORMAL_MID_RIGHT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_normal_mid_right, parent,false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.NORMAL_LEFT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_normal_left,parent,false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }
            GameLevel.NORMAL_RIGHT -> object : BaseLevelViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.level_normal_right, parent, false), gameLevels, isGameWon
            ){
                init {
                    val levelView = itemView.findViewById<ImageView>(R.id.levelView)
                    levelView.setOnClickListener {

                        val level: RealGameLevel? = levelList[bindingAdapterPosition] as? RealGameLevel
                        if (level != null) {
                            val levelData: RealGameLevel = level
                            val levelNumber = levelData.level
                            val levelWon = levelData.isCompleted

                            if(levelNumber > nextLevel || levelNumber > effectiveLevels) return@setOnClickListener

                            mListener?.onGameLevelClicked(levelNumber, levelWon)
                        }

                    }
                }
            }

            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val level = levelList[position]

        if (holder is BaseLevelViewHolder && level is RealGameLevel) {
            holder.bindLevelData(level)

            if(level.level == mNextLevel){
                mLightBgView = holder.itemView.findViewById(R.id.lightBg)
            }

            if(isGameWon && level.level == mNextLevel - 1){
                mLightStarView = holder.itemView.findViewById(R.id.levelView)
            }

        }


    }

    override fun getItemCount() = levelList.size


    interface ClickListener {
        fun onGameLevelClicked(level: Int, isWon: Boolean)
    }

    fun setListener(listener: ClickListener) {
        this.mListener = listener
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        mLightBgView?.clearAnimation()
        mLightStarView?.clearAnimation()
    }
}