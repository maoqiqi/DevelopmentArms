package com.codearms.maoqiqi.app.data.source.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

import com.codearms.maoqiqi.app.data.TaskBean

/**
 * The Database that contains the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
@Database(entities = [TaskBean::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun tasksDAO(): TasksDAO

    companion object {
        @Volatile
        private var INSTANCE: TaskDataBase? = null

        private val lock = Any()

        @JvmStatic
        internal fun getInstance(context: Context): TaskDataBase = INSTANCE ?: synchronized(lock) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                    TaskDataBase::class.java, "room_tasks.db").build().also { INSTANCE = it }
        }
    }
}