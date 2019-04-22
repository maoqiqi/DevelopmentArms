package com.codearms.maoqiqi.app.data.source.remote

import android.os.Handler
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Access the server API interface data as a data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:42
 */
class TasksRemoteDataSource private constructor() : TasksDataSource {

    /**
     * [TasksDataSource.LoadTasksCallBack.onDataNotAvailable] is never fired.
     * In a real remote data source implementation, this would be fired
     * if the server can't be contacted or the server returns an error.
     */
    override fun loadTasks(callBack: TasksDataSource.LoadTasksCallBack) {
        // Simulate network by delaying the execution.
        Handler().postDelayed({
            callBack.onTasksLoaded(ArrayList(TASKS_SERVICE_DATA.values))
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    /**
     * [TasksDataSource.GetTaskCallBack.onDataNotAvailable] is never fired.
     * In a real remote data source implementation, this would be fired
     * if the server can't be contacted or the server returns an error.
     */
    override fun getTask(taskId: String, callBack: TasksDataSource.GetTaskCallBack) {
        val taskBean = TASKS_SERVICE_DATA[taskId]
        Handler().postDelayed({
            taskBean?.let { callBack.onTaskLoaded(it) }
        }, SERVICE_LATENCY_IN_MILLIS)
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
        private const val SERVICE_LATENCY_IN_MILLIS = 5000L

        @Volatile
        private var INSTANCE: TasksRemoteDataSource? = null

        private var TASKS_SERVICE_DATA: MutableMap<String, TaskBean> = LinkedHashMap(2)

        init {
            addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
            addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
        }

        @JvmStatic
        fun getInstance(): TasksRemoteDataSource = INSTANCE
                ?: synchronized(TasksRemoteDataSource::class.java) {
                    INSTANCE ?: TasksRemoteDataSource().also { INSTANCE = it }
                }

        private fun addTask(title: String, description: String) {
            val taskBean = TaskBean(title, description, false)
            TASKS_SERVICE_DATA[taskBean.id] = taskBean
        }
    }
}