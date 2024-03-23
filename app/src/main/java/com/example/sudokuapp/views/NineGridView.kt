package com.example.sudokuapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.sudokuapp.R
import com.example.sudokuapp.SudokuApplication
import com.example.sudokuapp.data.BoardType
import com.example.sudokuapp.data.GameMode
import com.example.sudokuapp.data.OperationType
import com.example.sudokuapp.data.SameNumberCase
import com.example.sudokuapp.data.SoundType
import com.example.sudokuapp.data.SudokuOperation
import com.example.sudokuapp.utils.DatabaseManager
import com.example.sudokuapp.utils.SoundManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Stack
import java.util.concurrent.ConcurrentHashMap

class NineGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var mBoardData: Array<Array<Int>>? = null
    private var mDefaultData: Array<Array<Int>>? = null

    private var mClickX: Float = 0f
    private var mClickY: Float = 0f
    private var mIsClicked: Boolean = false
    private var mClickedRow = -1
    private var mClickedColumn = -1
    private var mCellSize: Float = 0f

    private val mBackground = BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_bg)
    private val mDefaultBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_filled_off)
    private val mCustomBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_none_off)
    private val mClickDefaultBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_filled_on)
    private val mClickCustomBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_none_on)
    private val mFocusOffBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_focus_off)
    private val mFocusOnBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_focus_on)
    private val mWrongFocusBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_focus_clash)
    private val mWrongDefaultBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_filled_clash)
    private val mWrongCustomBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_grid_none_clash)
    private val mKeyboardSmallBgBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_numberbox_bg1)
    private val mKeyboardBigBgBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_numberbox_bg2)
    private val mKeyboardMarkOffBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_numberbox_markoff)
    private val mKeyboardMarkOnBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_numberbox_markon)
    private val mKeyboardRevokeBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_play_numberbox_revoke)

    private var mBtnSize: Float = 0f
    private var mAdditionalSpacing: Float = 0f
    private var mKeyboardRow: Int = -1
    private var mKeyboardPressedIndex: Int = -1
    private var mKeyboardPressedNumber: Int = -1
    private var mIsMarked: Boolean = false
    private var mIsRevoke: Boolean = false
    private var mKeyboardEnable: Boolean = false
    private var mMarkEnable: Boolean = false
    private var mMarkCellSize: Float = 0f

    private var mModeNow: GameMode = GameMode.NORMAL

    private lateinit var mCanvas: Canvas
    private lateinit var mTextPaint: Paint
    private lateinit var mKeyboardTextPaint: Paint
    private lateinit var mKeyboardGrayBgPaint: Paint
    private lateinit var mGrayBgColorFilter: PorterDuffColorFilter

    private var mMarkRecords: ConcurrentHashMap<Pair<Int, Int>, MutableSet<Int>> =
        ConcurrentHashMap()

    private var mOperationRecords: Stack<SudokuOperation> = Stack()

    private var mRowArray = Array(9) { BooleanArray(9) }
    private var mColArray = Array(9) { BooleanArray(9) }
    private var mBoxArray = Array(9) { BooleanArray(9) }

    private var mListener: NineGridViewListener? = null

    /* Layout Translate */
    private var mHorizontalOffset: Float = 0f
    private var mVerticalOffset: Float = 0f

    /* Keyboard Position */
    private var mFirstRowTop: Float = 0f
    private var mFirstRowBottom: Float = 0f
    private var mSecondRowTop: Float = 0f
    private var mSecondRowBottom: Float = 0f

    /* Wrong Answer Animation */
    private val mWrongAnsDelayTime = 500L
    private var mJob: Job? = null

    private var mClickEnable = true


    companion object {
        @JvmStatic
        fun setBoardData(nineGridView: NineGridView, level: Int) {

            nineGridView.mBoardData = null
            nineGridView.mDefaultData = null

            nineGridView.mClickX = 0f
            nineGridView.mClickY = 0f
            nineGridView.mIsClicked = false
            nineGridView.mClickedRow = -1
            nineGridView.mClickedColumn = -1
            nineGridView.mCellSize = 0f

            nineGridView.mBtnSize = 0f
            nineGridView.mAdditionalSpacing = 0f
            nineGridView.mKeyboardRow = -1
            nineGridView.mKeyboardPressedIndex = -1
            nineGridView.mKeyboardPressedNumber = -1
            nineGridView.mIsMarked = false
            nineGridView.mIsRevoke = false
            nineGridView.mKeyboardEnable = false
            nineGridView.mMarkEnable = false
            nineGridView.mMarkCellSize = 0f
            nineGridView.mModeNow = GameMode.NORMAL

            nineGridView.mMarkRecords = ConcurrentHashMap()
            nineGridView.mOperationRecords = Stack()

            nineGridView.mRowArray = Array(9) { BooleanArray(9) }
            nineGridView.mColArray = Array(9) { BooleanArray(9) }
            nineGridView.mBoxArray = Array(9) { BooleanArray(9) }

            nineGridView.mListener = null

            nineGridView.mHorizontalOffset = 0f
            nineGridView.mVerticalOffset = 0f

            nineGridView.mFirstRowTop = 0f
            nineGridView.mFirstRowBottom = 0f
            nineGridView.mSecondRowTop = 0f
            nineGridView.mSecondRowBottom = 0f

            nineGridView.mJob = null

            nineGridView.mClickEnable = true

             runBlocking {
                try {
                    val data = DatabaseManager.getGameBoard(SudokuApplication.context, level)
                    nineGridView.mBoardData = data[BoardType.GAME_BOARD]
                    nineGridView.mDefaultData = data[BoardType.DEFAULT_MATRIX]
                } catch (e: Exception) {
                    Log.e("Sudoku", "An error occurred: ${e.message}")
                }
            }


            nineGridView.preprocessing()
            nineGridView.invalidate()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        /* Called when the View is attached to a window */
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvas = canvas

        /* Layout Position */
        val width = width.toFloat()
        val height = height.toFloat()
        mCellSize = width / 10
        mHorizontalOffset = (width - 9 * mCellSize) / 2
        mVerticalOffset = (height - 9 * mCellSize) / 3.5f + mCellSize / 2

        canvas.translate(mHorizontalOffset , mVerticalOffset)

        mBtnSize = mCellSize * 1.5f
        mMarkCellSize = mCellSize / 3
        mAdditionalSpacing = mCellSize * 0.125f

        mFirstRowTop = 11 * mCellSize + mAdditionalSpacing * 2
        mFirstRowBottom =  mFirstRowTop + mBtnSize
        mSecondRowTop = 13 * mCellSize - mAdditionalSpacing * 2
        mSecondRowBottom = mSecondRowTop + mBtnSize



        /* Game board text features */
        mTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = mCellSize * 0.5f
            textAlign = Paint.Align.CENTER
        }

        /* Keyboard text features */
        mKeyboardTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = mBtnSize * 0.5f
            textAlign = Paint.Align.CENTER
        }

        /* Game board background */
        val bgRectF = RectF(
            -0.5f * mCellSize,
            -0.5f * mCellSize,
            (9 + 0.5f) * mCellSize,
            (9 + 0.5f) * mCellSize
        )
        canvas.drawBitmap(mBackground, null, bgRectF, null)


        /* Mark Mode */
        if (mModeNow == GameMode.MARK && mClickedRow != -1 && mClickedColumn != -1) {
            markMode()
        }

        /* Draw grid items */
        init()


        /* Grid click event - Mode: Normal & Mark */
        if (mIsClicked) {
            gridItemClickEvent()
        }

        /* Keyboard Background */
        mKeyboardGrayBgPaint = Paint()
        mGrayBgColorFilter = PorterDuffColorFilter(
            if (!mKeyboardEnable) Color.GRAY else Color.WHITE,
            PorterDuff.Mode.SRC_IN
        )
        mKeyboardGrayBgPaint.colorFilter = mGrayBgColorFilter


        /* Keyboard click event - Filled numbers */
        if (mModeNow == GameMode.NORMAL && mKeyboardPressedNumber != -1) {

            keyboardClick(mKeyboardRow)

            val filledValue = mBoardData?.get(mClickedRow)?.get(mClickedColumn)

            if(filledValue == mKeyboardPressedNumber) undoFillNumber(filledValue) else filledNumberEvent()
        }


        /* Draw keyboard */
        setKeyboard()

        /* Keyboard button on or off */
        markEnable()
        revokeEnable()


        /* Draw grid lines */
        setLine()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                mClickX = (event.x - mHorizontalOffset)
                mClickY = (event.y - mVerticalOffset)

                if(!mClickEnable){
                    return false
                } else if (mClickX < 0 || mClickY < 0) {
                    return false
                } else if ( mClickY < 9 * mCellSize ) {
                    /* Sound Effect */
                    SoundManager.playSound(SoundType.BUTTON_TAP)

                    /* Grid Item on the Game Board - Touch Event */
                    mClickedRow = ((mClickY / mCellSize).toInt()).coerceIn(0, 8)
                    mClickedColumn = ((mClickX / mCellSize).toInt()).coerceIn(0, 8)

                    mIsClicked = true

                    /* Reset */
                    mModeNow = GameMode.NORMAL
                    mKeyboardPressedNumber = -1

                    invalidate()

                    return true
                } else if (mKeyboardEnable && mClickY >= mFirstRowTop && mClickY < mFirstRowBottom) {
                    /* First Row on the Keyboard - Touch Event */

                    /* Sound Effect */
                    SoundManager.playSound(SoundType.BUTTON_TAP)

                    val btnIndex = (mClickX / mBtnSize).toInt()

                    mKeyboardRow = 0
                    mKeyboardPressedIndex = btnIndex

                    /* Mark button event and Number button event*/
                    if (btnIndex != 5) {
                        mKeyboardPressedNumber = btnIndex + 1
                        if (mModeNow != GameMode.MARK) mModeNow = GameMode.NORMAL
                    } else{
                        mModeNow = if (mModeNow != GameMode.MARK && mMarkEnable) GameMode.MARK else GameMode.NORMAL

                        /* Reset */
                        mKeyboardPressedNumber = -1
                    }

                    invalidate()
                    return true
                } else if (mClickY >= mSecondRowTop && mClickY < mSecondRowBottom) {
                    /* Second Row on the Keyboard - Touch Event */

                    val btnIndex = (mClickX / mBtnSize).toInt()

                    mKeyboardRow = 1
                    mKeyboardPressedIndex = btnIndex

                    /* Revoke button event and number button event */
                    if (mKeyboardEnable && btnIndex != 4 && btnIndex != 5) {
                        mKeyboardPressedNumber = btnIndex + 6

                        SoundManager.playSound(SoundType.BUTTON_TAP)

                        if (mModeNow != GameMode.MARK) mModeNow = GameMode.NORMAL
                        invalidate()
                    } else if(btnIndex == 4 || btnIndex == 5){
                        if(isRevokeEnable()){
                            SoundManager.playSound(SoundType.BUTTON_TAP)
                        }
                        mModeNow = GameMode.REVOKE
                        revokeOperation()
                        invalidate()
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun init() {
        /* Draw Grid Items */
        for (i in 0 until 9) {
            for (j in 0 until 9) {
                val bitmap =
                    if (mDefaultData?.get(i)?.get(j) == 0) mCustomBitmap else mDefaultBitmap
                mCanvas.drawBitmap(bitmap, null, getRectF(i, j), null)

                val key = Pair(i, j)

                if (mBoardData?.get(i)?.get(j) != 0) {
                    setText(i, j, null)
                } else if (mMarkRecords[key] != null) {
                    setMark(i, j)
                }
            }
        }
    }

    private fun markMode() {
        /* Mark Event */
        val boardValue = mBoardData?.get(mClickedRow)?.get(mClickedColumn)

        if (boardValue == 0) {
            val key = Pair(mClickedRow, mClickedColumn)

            if (!mMarkRecords.containsKey(key)) {
                mMarkRecords[key] = mutableSetOf()
            }

            when {
                mKeyboardPressedNumber != -1 -> {
                    if (mMarkRecords.containsKey(key)) {
                        val set = mMarkRecords[key]
                        if (set != null) {
                            if (set.contains(mKeyboardPressedNumber)) {
                                set.remove(mKeyboardPressedNumber)
                                recordOperation(
                                    SudokuOperation(
                                        OperationType.UNDO_MARK,
                                        mKeyboardPressedNumber,
                                        mClickedRow,
                                        mClickedColumn
                                    )
                                )
                            } else {
                                set.add(mKeyboardPressedNumber)
                                recordOperation(
                                    SudokuOperation(
                                        OperationType.MARK_CELL,
                                        mKeyboardPressedNumber,
                                        mClickedRow,
                                        mClickedColumn
                                    )
                                )
                            }
                        }
                    } else {
                        val set = ConcurrentHashMap.newKeySet<Int>()
                        set.add(mKeyboardPressedNumber)
                        mMarkRecords[key] = set
                        recordOperation(
                            SudokuOperation(
                                OperationType.MARK_CELL,
                                mKeyboardPressedNumber,
                                mClickedRow,
                                mClickedColumn
                            )
                        )
                    }
                    setMark(mClickedRow, mClickedColumn)
                }
            }
        }
    }

    private fun gridItemClickEvent() {
        /* Grid item click then update layout */
        val defaultBoardData = mDefaultData?.get(mClickedRow)?.get(mClickedColumn)
        val boardData = mBoardData?.get(mClickedRow)?.get(mClickedColumn)

        if (defaultBoardData == 0 && boardData == 0) {
            /* Grid item without number - click event */
            mKeyboardEnable = true
            mMarkEnable = true

            /* Draw Row, Col and Grid */
            noneGridClick()

            /* Show the data of the Mark mode */
            val key = Pair(mClickedRow, mClickedColumn)
            if (mMarkRecords[key] != null) setMark(mClickedRow, mClickedColumn)

        }
        else if (boardData != 0) {
            /* Grid item with number - click event */
            mMarkEnable = false
            mKeyboardEnable = defaultBoardData != 1

            /* Find all the same numbers on the game board */
            filledGridClick(SameNumberCase.CLICK_TO_VIEW)

            /* Clicked grid item layout */
            if(defaultBoardData == 0){
                mCanvas.drawBitmap(
                    mFocusOnBitmap,
                    null,
                    getRectF(mClickedRow, mClickedColumn),
                    null
                )
                setText(mClickedRow, mClickedColumn, null)
            }
        }
    }

    private fun undoFillNumber(value: Int){
        /* Click on the filled-in grid item and click on the same number on the keyboard */
        clearCorrectNumberOperation(value)

        /* Draw Row, Col and Grid */
        init()
        noneGridClick()

        mKeyboardEnable = true
        mMarkEnable = true
        mKeyboardPressedNumber=-1

        /* Show the data of the Mark mode */
        val key = Pair(mClickedRow, mClickedColumn)
        if (mMarkRecords[key] != null) setMark(mClickedRow, mClickedColumn)

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun filledNumberEvent() {
        /* Fill in a number then judge it */
        val value = mKeyboardPressedNumber
        val isWrong = isExist(value)


        if (isWrong) {
            /* Sound Effect */
            SoundManager.playSound(SoundType.WRONG_PLACEMENT)

            /* Update View */
            init()
            noneGridClick()
            filledGridClick(SameNumberCase.ERROR_FILL)

            /* Wrong fill-in number disappear */
//            handler.postDelayed({
//                mKeyboardPressedNumber = -1
//                init()
//                invalidate()
//            }, mWrongAnsDelayTime)

            mJob = GlobalScope.launch {
                delay(mWrongAnsDelayTime)

                withContext(Dispatchers.Main) {
                    mKeyboardPressedNumber = -1
                    init()
                    invalidate()
                }
            }

        } else {

            /* Delete the previous correct number */
            val exNumber = mBoardData?.get(mClickedRow)?.get(mClickedColumn)
            if(exNumber != null && exNumber!=0 ){
                clearCorrectNumberOperation(exNumber)
            }

            /* Fill in the correct number */
            correctNumberOperation(value)

            /* Update View */
            init()
            filledGridClick(SameNumberCase.CLICK_TO_VIEW)
            mCanvas.drawBitmap(
                mFocusOnBitmap,
                null,
                getRectF(mClickedRow, mClickedColumn),
                null
            )
            setText(mClickedRow, mClickedColumn, value)

            /* Unable to switch to the Mark mode in the clicked grid item */
            mMarkEnable = false
        }

        /* Game won Event */
        if(!isWrong && isWon()){
            mClickEnable = false
            mListener?.onIsWon(true)

        }

    }

    private fun correctNumberOperation(value: Int){
        /* Store Value & Record Operation & Update Bitmask */
        mBoardData?.get(mClickedRow)?.set(mClickedColumn, value)

        recordOperation(
            SudokuOperation(
                OperationType.FILL_NUMBER,
                value,
                mClickedRow,
                mClickedColumn
            )
        )

        updateBitmask(mClickedRow, mClickedColumn, value, false)
    }

    private fun clearCorrectNumberOperation(value: Int){
        /* Substitute Zero & Record Operation & Update Bitmask */
        mBoardData?.get(mClickedRow)?.set(mClickedColumn,0)

        recordOperation(
            SudokuOperation(
                OperationType.UNDO_FILL_NUMBER,
                value,
                mClickedRow,
                mClickedColumn
            )
        )

        updateBitmask(mClickedRow, mClickedColumn, value, true)
    }

    private fun setLine() {
        /* Draw lines */
        val linePaint = Paint()
        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 1f


        for (i in 0 until 10) {
            /* 3*3 grid line or other grid line */
            linePaint.strokeWidth = if (i % 3 == 0) 3f else 1f

            mCanvas.drawLine(i * mCellSize, 0f, i * mCellSize, 9 * mCellSize, linePaint)
            mCanvas.drawLine(0f, i * mCellSize, 9 * mCellSize, i * mCellSize, linePaint)
        }

    }

    private fun setKeyboard() {
        /* Draw first row on the keyboard */
        for (i in 0 until 5) {
            val left = i * mBtnSize - mAdditionalSpacing
            val right = left + mBtnSize
            val top = mFirstRowTop
            val bottom = mFirstRowBottom
            val rect = RectF(left, top, right, bottom)

            mCanvas.drawBitmap(mKeyboardSmallBgBitmap, null, rect, mKeyboardGrayBgPaint)
            val value = i + 1
            drawKeyboardText(left, top, value)
        }

        /* Draw second row on the keyboard */
        for (i in 0 until 4) {
            val left = i * mBtnSize  - mAdditionalSpacing
            val right = left + mBtnSize
            val top = mSecondRowTop
            val bottom = mSecondRowBottom
            val rect = RectF(left, top, right, bottom)

            mCanvas.drawBitmap(mKeyboardSmallBgBitmap, null, rect, mKeyboardGrayBgPaint)
            val value = i + 6
            drawKeyboardText(left, top, value)
        }

        if (mIsClicked && mClickedRow != -1 && mClickedColumn != -1) {
            val boardData = mBoardData?.get(mClickedRow)?.get(mClickedColumn)
            if (boardData!= 0) {
                boardData?.let { numberDisable(it) }
            }
        }
    }

    private fun drawKeyboardText(left: Float, top: Float, value: Int) {
        /* Draw text on the keyboard */
        val metrics = mKeyboardTextPaint.fontMetrics
        val textHeight = metrics.descent - metrics.ascent
        val textOffset = (textHeight / 2) - metrics.descent
        val x = left + mBtnSize / 2
        val y = top + mBtnSize / 2 + textOffset

        mCanvas.drawText("$value", x, y, mKeyboardTextPaint)
    }

    private fun revokeEnable() {
        /* The layout of Revoke button on the keyboard */
        val left = 4 * mBtnSize - mAdditionalSpacing
        val right = left + mBtnSize * 2
        val top = mSecondRowTop
        val bottom = mSecondRowBottom

        val color = if (!isRevokeEnable()) {
            Color.GRAY
        } else {
            Color.WHITE
        }

        mGrayBgColorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        mKeyboardGrayBgPaint.colorFilter = mGrayBgColorFilter

        val bigRect = RectF(left, top, right, bottom)
        val imgWidth = (bigRect.right - bigRect.left) / 2
        val imgRect = RectF(
            bigRect.left + (bigRect.width() - imgWidth) / 2,
            top,
            bigRect.right - (bigRect.width() - imgWidth) / 2,
            bottom
        )
        mCanvas.drawBitmap(mKeyboardBigBgBitmap, null, bigRect, mKeyboardGrayBgPaint)
        mCanvas.drawBitmap(mKeyboardRevokeBitmap, null, imgRect, null)
    }

    private fun markEnable() {
        /* The layout of Mark button on the keyboard */
        val left = 5 * mBtnSize - mAdditionalSpacing
        val right = left + mBtnSize

        /* Background color */
        val color = if (!mMarkEnable) {
            Color.GRAY
        } else {
            Color.WHITE
        }

        /* Background image */
        val bitmap = if(!mMarkEnable){
            mKeyboardMarkOffBitmap
        }
        else{
            if (mModeNow == GameMode.MARK) mKeyboardMarkOnBitmap else mKeyboardMarkOffBitmap
        }

        mGrayBgColorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        mKeyboardGrayBgPaint.colorFilter = mGrayBgColorFilter

        val rect = RectF(left, mFirstRowTop, right, mFirstRowBottom)
        val imgWidth = (rect.right - rect.left) / 2
        val imgRect = RectF(
            rect.left + (rect.width() - imgWidth) / 2,
            mFirstRowTop,
            rect.right - (rect.width() - imgWidth) / 2,
            mFirstRowBottom
        )

        mCanvas.drawBitmap(mKeyboardSmallBgBitmap, null, rect, mKeyboardGrayBgPaint)
        mCanvas.drawBitmap(bitmap, null, imgRect, null)
    }

    private fun numberDisable(number: Int) {
        /* The background of the clicked button of a certain number */
        mGrayBgColorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        mKeyboardGrayBgPaint.colorFilter = mGrayBgColorFilter

        if (number < 6) {
            val left = (number - 1) * mBtnSize - mAdditionalSpacing
            val right = left + mBtnSize
            val rect = RectF(left, mFirstRowTop, right, mFirstRowBottom)

            mCanvas.drawBitmap(mKeyboardSmallBgBitmap, null, rect, mKeyboardGrayBgPaint)
            drawKeyboardText(left, mFirstRowTop, number)
        } else {
            val left = (number - 6) * mBtnSize - mAdditionalSpacing
            val right = left + mBtnSize
            val rect = RectF(left, mSecondRowTop, right, mSecondRowBottom)

            mCanvas.drawBitmap(mKeyboardSmallBgBitmap, null, rect, mKeyboardGrayBgPaint)
            drawKeyboardText(left, mSecondRowTop, number)
        }
    }


    private fun noneGridClick() {
        /* NoneGrid: the grid without a number */

        /* Column and row layout */
        for (i in 0 until 9) {

            if (mDefaultData?.get(mClickedColumn)?.get(i) == 0) {
                mCanvas.drawBitmap(mClickCustomBitmap, null, getRectF(i, mClickedColumn), null)
            } else {
                mCanvas.drawBitmap(mClickDefaultBitmap, null, getRectF(i, mClickedColumn), null)
            }

            if (mDefaultData?.get(i)?.get(mClickedRow) == 0) {
                mCanvas.drawBitmap(mClickCustomBitmap, null, getRectF(mClickedRow, i), null)
            } else {
                mCanvas.drawBitmap(mClickDefaultBitmap, null, getRectF(mClickedRow, i), null)
            }

            val keyRow = Pair(i, mClickedColumn)

            if (mBoardData?.get(i)?.get(mClickedColumn) != 0) {
                setText(i, mClickedColumn, null)
            } else if (mMarkRecords[keyRow] != null) {
                setMark(i, mClickedColumn)
            }

            val keyCol = Pair(mClickedRow, i)
            if (mBoardData?.get(mClickedRow)?.get(i) != 0) {
                setText(mClickedRow, i, null)
            } else if (mMarkRecords[keyCol] != null) {
                setMark(mClickedRow, i)
            }

        }

        /* 3*3 Grid layout */
        val boxStartRow = mClickedRow / 3 * 3
        val boxStartCol = mClickedColumn / 3 * 3

        for (i in boxStartRow until boxStartRow + 3) {
            for (j in boxStartCol until boxStartCol + 3) {
                if (i != mClickedRow || j != mClickedColumn) {
                    if (mDefaultData?.get(i)?.get(j) == 0) {
                        val key = Pair(i, j)
                        mCanvas.drawBitmap(mClickCustomBitmap, null, getRectF(i, j), null)
                        if (mBoardData?.get(i)?.get(j) != 0) {
                            setText(i, j, null)
                        } else if (mMarkRecords[key] != null) {
                            setMark(i, j)
                        }
                    } else {
                        mCanvas.drawBitmap(mClickDefaultBitmap, null, getRectF(i, j), null)
                        setText(i, j, null)
                    }
                }
            }
        }

        /* Clicked grid item layout */
        if (mDefaultData?.get(mClickedRow)?.get(mClickedColumn) == 0) {
            mCanvas.drawBitmap(
                mFocusOffBitmap,
                null,
                getRectF(mClickedRow, mClickedColumn),
                null
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun filledGridClick(case: SameNumberCase) {
        /* Click the grid item with a number*/

        val value: Int
        var bitmap: Bitmap

        if(case == SameNumberCase.ERROR_FILL){
            value = mKeyboardPressedNumber

            for(i in 0 until 9){
                for(j in 0 until 9){
                    if(value == mBoardData?.get(i)?.get(j)){

                        if(i!=mClickedRow && j!=mClickedColumn){
                            bitmap = if (mDefaultData?.get(i)
                                    ?.get(j) == 0
                            ) mClickCustomBitmap else mClickDefaultBitmap
                        }
                        else{
                            bitmap = if (mDefaultData?.get(i)
                                    ?.get(j) == 0
                            ) mWrongCustomBitmap else mWrongDefaultBitmap
                        }

                        mCanvas.drawBitmap(bitmap, null, getRectF(i, j), null)
                        setText(i, j, null)
                    }
                }
            }

            /* Clicked grid item layout */
            mCanvas.drawBitmap(
                mWrongFocusBitmap,
                null,
                getRectF(mClickedRow, mClickedColumn),
                null
            )
            setText(mClickedRow, mClickedColumn, mKeyboardPressedNumber)

        }
        else if(case == SameNumberCase.CLICK_TO_VIEW){
            value = mBoardData?.get(mClickedRow)?.get(mClickedColumn)!!

            for(i in 0 until  9){
                for(j in 0 until 9){
                    if(value == mBoardData?.get(i)?.get(j)){
                        bitmap = if (mDefaultData?.get(i)?.get(j) == 0) mClickCustomBitmap else mClickDefaultBitmap
                        Log.d("default", mDefaultData?.get(i)?.get(j).toString())
                        mCanvas.drawBitmap(bitmap, null, getRectF(i, j), null)
                        setText(i, j, null)
                    }
                }
            }

        }
    }

    private fun keyboardClick(row: Int) {
        /* Change button background */
        /* Redraw text in specific positions */

        /* Common Features */
        val bgPaint = Paint()
        val colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        bgPaint.colorFilter = colorFilter

        val top = if(row == 0) mFirstRowTop * 2 else mSecondRowTop
        val left = mKeyboardPressedIndex * mBtnSize - mAdditionalSpacing
        val right = left + mBtnSize
        val bottom = top + mBtnSize
        val rect = RectF(left, top, right, bottom)

        when {
            row == 0 && mKeyboardPressedIndex == 5 && mMarkEnable -> {
                val imgWidth = (rect.right - rect.left) / 2
                val imgRect = RectF(
                    rect.left + (rect.width() - imgWidth) / 2,
                    top,
                    rect.right - (rect.width() - imgWidth) / 2,
                    bottom
                )
                val imgBitmap = if (mIsMarked) mKeyboardMarkOnBitmap else mKeyboardMarkOffBitmap
                mCanvas.drawBitmap(imgBitmap, null, imgRect, null)
            }
            row == 1 && (mKeyboardPressedIndex == 4 || mKeyboardPressedIndex == 5) -> {
                val revokeLeft = 4 * mBtnSize - mAdditionalSpacing
                val revokeRight = revokeLeft + mBtnSize * 2

                val bigRect = RectF(revokeLeft, mSecondRowTop, revokeRight, mSecondRowBottom)
                val imgWidth = (bigRect.right - bigRect.left) / 2
                val imgRect = RectF(
                    bigRect.left + (bigRect.width() - imgWidth) / 2,
                    mSecondRowTop,
                    bigRect.right - (bigRect.width() - imgWidth) / 2,
                    mSecondRowBottom
                )
                mCanvas.drawBitmap(mKeyboardBigBgBitmap, null, bigRect, bgPaint)
                mCanvas.drawBitmap(mKeyboardRevokeBitmap, null, imgRect, null)
            }
            else -> {
                val rowLeft = mKeyboardPressedIndex * mBtnSize - mAdditionalSpacing
                val rowTop = if(row == 0) mFirstRowTop else mSecondRowTop
                drawKeyboardText(rowLeft,rowTop,mKeyboardPressedNumber)
            }
        }
    }


    private fun getRectF(row: Int, col: Int): RectF {
        /* Draw rectF */
        val left = col * mCellSize
        val top = row * mCellSize
        val right = left + mCellSize
        val bottom = top + mCellSize

        return RectF(
            left,
            top,
            right,
            bottom
        )
    }


    private fun setText(row: Int, col:Int, filledValue: Int?){
        /* Set text on the grid item */
        val left = col * mCellSize
        val top = row * mCellSize

        val metrics = mTextPaint.fontMetrics
        val textHeight = metrics.descent - metrics.ascent
        val textOffset = (textHeight / 2) - metrics.descent

        val x = left + mCellSize / 2
        val y = top + mCellSize / 2 + textOffset

        /* filledValue is not null means that the case is filled to the correct number */
        if(filledValue != null){
            mCanvas.drawText(mKeyboardPressedNumber.toString(),x,y,mTextPaint)
        }
        else{
            val textValue = mBoardData?.get(row)?.get(col).toString()
            mCanvas.drawText(textValue,x,y,mTextPaint)
        }
    }

    private fun setMark(row: Int, col: Int) {
        /* To mark a specific grid item */
        val markPaint = Paint().apply {
            color = Color.GRAY
            textSize = mMarkCellSize * 0.8f
            textAlign = Paint.Align.CENTER
        }

        val key = Pair(row,col)
        val arrayList = mMarkRecords[key]

        arrayList?.forEach { element ->

            val i = (element - 1) / 3
            val j = (element - 1) % 3

            val left = col * mCellSize + j  * mMarkCellSize
            val top = row * mCellSize + i  * mMarkCellSize

            /* Draw Text */
            val metrics = markPaint.fontMetrics
            val textHeight = metrics.descent - metrics.ascent
            val textOffset = (textHeight / 2) - metrics.descent

            val x = left + mMarkCellSize / 2
            val y = top + mMarkCellSize / 2 + textOffset

            mCanvas.drawText(element.toString(),x,y,markPaint)
        }
    }

    private fun recordOperation(operation: SudokuOperation){
        mOperationRecords.push(operation)
    }

    private fun isRevokeEnable(): Boolean {
        mIsRevoke = !mOperationRecords.isEmpty()
        return mIsRevoke
    }

    private fun revokeOperation(){
        /* Check */
        if(!isRevokeEnable()){
            return
        }

        /* Common features */
        val poppedElement = mOperationRecords.pop()
        val row = poppedElement.row
        val col = poppedElement.col
        val type = poppedElement.type
        val value = poppedElement.value
        val key = Pair(row,col)

        when (type) {
            OperationType.FILL_NUMBER -> {
                mBoardData?.get(row)?.set(col, 0)
                updateBitmask(row, col, value, true)
            }
            OperationType.UNDO_FILL_NUMBER -> {
                mBoardData?.get(row)?.set(col, value)
                updateBitmask(row, col, value, false)
            }
            OperationType.MARK_CELL -> {
                mMarkRecords[key]?.remove(value)
            }
            OperationType.UNDO_MARK -> {
                mMarkRecords[key]?.add(value)
            }
        }

        mClickedRow = row
        mClickedColumn = col

    }

    private fun preprocessing(){
        /* Set bitmask for each row, each column, each box */
        for (i in 0 until 9) {
            for (j in 0 until 9) {

                val value = mBoardData?.get(i)?.get(j)

                if (value != 0) {
                    val num = value!!.minus(1)
                    val boxIndex = (i / 3) * 3 + j / 3
                    mRowArray[i][num] = true
                    mColArray[j][num] = true
                    mBoxArray[boxIndex][num] = true
                }
            }
        }
    }

    private fun updateBitmask(row: Int, col: Int, value: Int, isRevoke: Boolean){
        val boxIndex = (row / 3) * 3 + col / 3
        val num = value - 1

        mRowArray[row][num] = !isRevoke
        mColArray[col][num] = !isRevoke
        mBoxArray[boxIndex][num] = !isRevoke
    }

    private fun isExist(value: Int): Boolean{
        /* Judge whether the number entered is right or wrong */
        var isWrong = false

        val row = mClickedRow
        val col = mClickedColumn
        val boxIndex = (row / 3) * 3 + col / 3
        val num = value - 1

        if(mRowArray[row][num] || mColArray[col][num] || mBoxArray[boxIndex][num]){
            /* the number exists -> wrong number */
            isWrong = true
        }

        return isWrong
    }

    private fun isWon(): Boolean{
        /* Judge whether the game is won or lost */
        for (i in 0 until 9) {
            for(j in 0 until 9){
                if(mBoardData?.get(i)?.get(j) == 0){
                    return false
                }
            }
        }
        return true
    }

    interface NineGridViewListener {
        fun onIsWon(data: Boolean)
    }

    fun setNineGridViewListener(listener: NineGridViewListener) {
        this.mListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mJob?.cancel()
    }
}



