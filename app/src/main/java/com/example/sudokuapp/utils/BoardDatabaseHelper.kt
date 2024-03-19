package com.example.sudokuapp.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BoardDatabaseHelper(val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    companion object {
        private const val DB_NAME = "GameBoard.db"
        private const val DB_VERSION = 1
    }

    private var DB_PATH: String = context.getDatabasePath(DB_NAME).path

    init {
        if (!isDatabaseExists()) {
            copyDatabaseFromAssets(context)
        }
    }

    private fun isDatabaseExists(): Boolean {
        val dbFile = File(DB_PATH)
        return dbFile.exists()
    }

    private fun copyDatabaseFromAssets(context: Context) {
            context.assets.open(DB_NAME).use { inputStream ->
                FileOutputStream(DB_PATH).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()
                }
            }
    }


    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}