package com.codearms.maoqiqi.app.data.source.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.codearms.maoqiqi.app.data.TaskBean;

/**
 * The Database that contains the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
@Database(entities = {TaskBean.class}, version = 1)
public abstract class TaskDataBase extends RoomDatabase {

    private static volatile TaskDataBase INSTANCE;

    private static final Object lock = new Object();

    static TaskDataBase getInstance(Context context) {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TaskDataBase.class, "room_tasks.db").build();
            }
        }
        return INSTANCE;
    }

    abstract TasksDAO tasksDAO();
}