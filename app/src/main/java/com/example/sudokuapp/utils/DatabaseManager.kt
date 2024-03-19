package com.example.sudokuapp.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.sudokuapp.data.BoardType
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DatabaseManager {

    @SuppressLint("Range")
    suspend fun getGameBoard(context: Context, level: Int):MutableMap<BoardType, Array<Array<Int>>> = coroutineScope {
            val dbHelper = BoardDatabaseHelper(context)
            val db = dbHelper.writableDatabase
            val result = mutableMapOf<BoardType,Array<Array<Int>>>()

            val cursor = db.rawQuery("SELECT * FROM Board WHERE level = $level", null)
            if (cursor.moveToFirst()) {
                    val boardValue = cursor.getString(cursor.getColumnIndex("board"))
                    val defaultMatrix = cursor.getString(cursor.getColumnIndex("isDefault"))

                    result[BoardType.GAME_BOARD] = textToArray(boardValue)
                    result[BoardType.DEFAULT_MATRIX] = textToArray(defaultMatrix)
            }

            db.close()
            cursor.close()

            return@coroutineScope result
    }

    private suspend fun textToArray(data: String): Array<Array<Int>> = suspendCoroutine{  continuation ->
        val trimmedText = data.substring(2, data.length - 2)
        val rows = trimmedText.split("],[")

        val numArray = rows.map { row ->
            row.split(",").map { it.toInt() }.toTypedArray()
        }.toTypedArray()


        numArray.forEach { row ->
            println(row.joinToString())
        }

        continuation.resume(numArray)
        //return numArray
    }
}