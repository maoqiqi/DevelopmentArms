package com.codearms.maoqiqi.app

import android.content.Context
import androidx.room.Room
import com.codearms.maoqiqi.app.data.source.TaskDataSource
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.data.source.remote.FakeTaskRemoteDataSource
import com.codearms.maoqiqi.app.data.source.room.TaskDataBase
import com.codearms.maoqiqi.app.data.source.room.TaskRoomDataSource

/**
 * Enables injection of mock implementations for [TaskDataSource] at compile time.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 16:42
 */
object Injection {

    private var database: TaskDataBase? = null

    fun provideTasksRepository(context: Context): TaskRepository {
        return TaskRepository(FakeTaskRemoteDataSource, createTaskLocalDataSource(context))
    }

    private fun createTaskLocalDataSource(context: Context): TaskDataSource {
        val database = database ?: createDataBase(context)
        return TaskRoomDataSource(database.tasksDAO())
    }

    private fun createDataBase(context: Context): TaskDataBase {
        database = Room.databaseBuilder(context.applicationContext, TaskDataBase::class.java, "room_tasks.db").build()
        return database as TaskDataBase
    }
}