package com.codearms.maoqiqi.app.data.source.remote

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import java.util.*

/**
 * Implement a fast access to the server API interface.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 15:15
 */
class FakeTasksRemoteDataSource private constructor() : TasksDataSource {

    override fun loadTasks(callBack: TasksDataSource.LoadTasksCallBack) {
        callBack.onTasksLoaded(ArrayList(TASKS_SERVICE_DATA.values))
    }

    override fun getTask(taskId: String, callBack: TasksDataSource.GetTaskCallBack) {
        TASKS_SERVICE_DATA[taskId]?.let {
            callBack.onTaskLoaded(it)
        }
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues { it.isActive } as LinkedHashMap
    }

    override fun refreshTasks() {

    }

    override fun addTask(taskBean: TaskBean) {
        TASKS_SERVICE_DATA[taskBean.id] = taskBean
    }

    override fun updateTask(taskBean: TaskBean) {
        TASKS_SERVICE_DATA[taskBean.id] = taskBean
    }

    override fun completeTask(completedTaskBean: TaskBean) {
        completeTask(completedTaskBean.id)
    }

    override fun completeTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]?.let {
            it.isCompleted = true
            TASKS_SERVICE_DATA[it.id] = it
        }
    }

    override fun activateTask(activeTaskBean: TaskBean) {
        activateTask(activeTaskBean.id)
    }

    override fun activateTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]?.let {
            it.isCompleted = false
            TASKS_SERVICE_DATA[it.id] = it
        }
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    companion object {

        private var INSTANCE: FakeTasksRemoteDataSource? = null

        private var TASKS_SERVICE_DATA = LinkedHashMap<String, TaskBean>()

        @JvmStatic
        fun getInstance(): FakeTasksRemoteDataSource = INSTANCE
                ?: synchronized(FakeTasksRemoteDataSource::class.java) {
                    INSTANCE ?: FakeTasksRemoteDataSource().also { INSTANCE = it }
                }
    }
}