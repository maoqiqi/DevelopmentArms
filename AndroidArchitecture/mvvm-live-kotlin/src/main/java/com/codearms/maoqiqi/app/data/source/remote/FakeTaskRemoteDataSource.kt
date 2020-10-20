package com.codearms.maoqiqi.app.data.source.remote

import com.codearms.maoqiqi.app.data.Result

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TaskDataSource
import java.lang.Exception
import java.util.*

/**
 * Implement a fast access to the server API interface.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 15:15
 */
object FakeTaskRemoteDataSource : TaskDataSource {

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, TaskBean>()

    override suspend fun loadTasks(): Result<List<TaskBean>> {
        return Result.Success(TASKS_SERVICE_DATA.values.toList())
    }

    override suspend fun getTask(taskId: String): Result<TaskBean> {
        TASKS_SERVICE_DATA[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Task not found!"))
    }

    override suspend fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues { it.isActive } as LinkedHashMap
    }

    override suspend fun refreshTasks() {

    }

    override suspend fun addTask(taskBean: TaskBean) {
        TASKS_SERVICE_DATA[taskBean.id] = taskBean
    }

    override suspend fun updateTask(taskBean: TaskBean) {
        TASKS_SERVICE_DATA[taskBean.id] = taskBean
    }

    override suspend fun completeTask(completedTaskBean: TaskBean) {
        completeTask(completedTaskBean.id)
    }

    override suspend fun completeTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]?.let {
            it.isCompleted = true
            TASKS_SERVICE_DATA[it.id] = it
        }
    }

    override suspend fun activateTask(activeTaskBean: TaskBean) {
        activateTask(activeTaskBean.id)
    }

    override suspend fun activateTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]?.let {
            it.isCompleted = false
            TASKS_SERVICE_DATA[it.id] = it
        }
    }

    override suspend fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }
}