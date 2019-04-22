package com.codearms.maoqiqi.app.data.source.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * The Database that contains the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
class DatabaseHelper internal constructor(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the task table
        db.execSQL("CREATE TABLE IF NOT EXISTS task (id varchar(20) primary key, title TEXT, description TEXT, completed INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        // Database name
        private const val NAME = "tasks"
        // Database version
        private const val VERSION = 1
    }
}