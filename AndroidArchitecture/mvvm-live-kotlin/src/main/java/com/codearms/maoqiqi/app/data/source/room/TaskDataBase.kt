package com.codearms.maoqiqi.app.data.source.room

import androidx.room.Database
import androidx.room.RoomDatabase

import com.codearms.maoqiqi.app.data.TaskBean

/**
 * The Database that contains the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
@Database(entities = [TaskBean::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun tasksDAO(): TaskDAO

}