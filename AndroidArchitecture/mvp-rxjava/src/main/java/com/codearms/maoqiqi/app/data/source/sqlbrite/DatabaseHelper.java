package com.codearms.maoqiqi.app.data.source.sqlbrite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * The Database that contains the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/4/1 13:52
 */
public final class DatabaseHelper extends SQLiteOpenHelper {

    // Database name
    private static final String NAME = "sqlbrite.db";

    // Database version
    private static final int VERSION = 1;

    DatabaseHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the task table
        db.execSQL("CREATE TABLE IF NOT EXISTS task (id varchar(20) primary key, title TEXT, description TEXT, completed INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}