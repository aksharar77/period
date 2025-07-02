package com.example.periodtracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PeriodTracker.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "FormAnswers"
        const val COLUMN_ID = "id"
        const val COLUMN_QUESTION = "question"
        const val COLUMN_ANSWER = "answer"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_QUESTION TEXT, "
                + "$COLUMN_ANSWER TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertAnswer(question: String, answer: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_QUESTION, question)
        contentValues.put(COLUMN_ANSWER, answer)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun getAllAnswers(): List<Pair<String, String>> {
        val answerList = ArrayList<Pair<String, String>>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION))
                val answer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER))
                answerList.add(Pair(question, answer))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return answerList
    }
}
