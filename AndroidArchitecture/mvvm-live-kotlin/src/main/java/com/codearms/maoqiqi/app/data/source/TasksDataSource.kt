package com.codearms.maoqiqi.app.data.source

import com.codearms.maoqiqi.app.data.TaskBean

/**
 * Main entry point for accessing tasks data.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
interface TasksDataSource {

    interface LoadTasksCallBack {

        fun onTasksLoaded(taskBeanList: List<TaskBean>)

        fun onDataNotAvailable()
    }

    interface GetTaskCallBack {

        fun onTaskLoaded(taskBean: TaskBean)

        fun onDataNotAvailable()
    }

    fun loadTasks(callBack: LoadTasksCallBack)

    fun getTask(taskId: String, callBack: GetTaskCallBack)

    fun clearCompletedTasks()

    fun refreshTasks()

    fun addTask(taskBean: TaskBean)

    fun updateTask(taskBean: TaskBean)

    fun completeTask(completedTaskBean: TaskBean)

    fun completeTask(taskId: String)

    fun activateTask(activeTaskBean: TaskBean)

    fun activateTask(taskId: String)

    fun deleteTask(taskId: String)

    fun deleteAllTasks()
}