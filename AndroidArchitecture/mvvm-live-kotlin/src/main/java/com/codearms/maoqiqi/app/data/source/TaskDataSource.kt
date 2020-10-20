package com.codearms.maoqiqi.app.data.source

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.Result

/**
 * Main entry point for accessing tasks data.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
interface TaskDataSource {

    suspend fun loadTasks(): Result<List<TaskBean>>

    suspend fun getTask(taskId: String): Result<TaskBean>

    suspend fun clearCompletedTasks()

    suspend fun refreshTasks()

    suspend fun addTask(taskBean: TaskBean)

    suspend fun updateTask(taskBean: TaskBean)

    suspend fun completeTask(completedTaskBean: TaskBean)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(activeTaskBean: TaskBean)

    suspend fun activateTask(taskId: String)

    suspend fun deleteTask(taskId: String)

    suspend fun deleteAllTasks()
}