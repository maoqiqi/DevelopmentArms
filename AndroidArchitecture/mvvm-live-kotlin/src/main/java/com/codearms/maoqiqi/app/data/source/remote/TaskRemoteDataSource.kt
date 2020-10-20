package com.codearms.maoqiqi.app.data.source.remote

import com.codearms.maoqiqi.app.data.Result
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TaskDataSource
import kotlinx.coroutines.delay
import java.lang.Exception
import kotlin.collections.LinkedHashMap

/**
 * Access the server API interface data as a data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:42
 */
object TaskRemoteDataSource : TaskDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASKS_SERVICE_DATA: MutableMap<String, TaskBean> = LinkedHashMap(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(title: String, description: String) {
        val taskBean = TaskBean(title = title, description = description)
        TASKS_SERVICE_DATA[taskBean.id] = taskBean
    }

    /**
     * In a real remote data source implementation, this would be fired
     * if the server can't be contacted or the server returns an error.
     */
    override suspend fun loadTasks(): Result<List<TaskBean>> {
        // Simulate network by delaying the execution.
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Result.Success(TASKS_SERVICE_DATA.values.toList())
    }

    /**
     * In a real remote data source implementation, this would be fired
     * if the server can't be contacted or the server returns an error.
     */
    override suspend fun getTask(taskId: String): Result<TaskBean> {
        delay(SERVICE_LATENCY_IN_MILLIS)
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