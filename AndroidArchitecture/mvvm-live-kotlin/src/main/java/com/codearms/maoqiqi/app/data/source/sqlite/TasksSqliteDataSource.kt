package com.codearms.maoqiqi.app.data.source.sqlite

import android.content.Context
import androidx.annotation.VisibleForTesting

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource

/**
 * The sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
class TasksSqliteDataSource private constructor(context: Context) : TasksDataSource {

    private val dao: TasksDAO = TasksDAO(context)

    override fun loadTasks(callBack: TasksDataSource.LoadTasksCallBack) {
        val list = dao.loadTasks()
        if (list.isEmpty()) {
            callBack.onDataNotAvailable()
        } else {
            callBack.onTasksLoaded(list)
        }
    }

    override fun getTask(taskId: String, callBack: TasksDataSource.GetTaskCallBack) {
        val taskBean = dao.getTaskById(taskId)
        if (taskBean == null) {
            callBack.onDataNotAvailable()
        } else {
            callBack.onTaskLoaded(taskBean)
        }
    }

    override fun clearCompletedTasks() {
        dao.deleteCompletedTasks()
    }

    override fun refreshTasks() {

    }

    override fun addTask(taskBean: TaskBean) {
        dao.addTask(taskBean)
    }

    override fun updateTask(taskBean: TaskBean) {
        dao.updateTask(taskBean)
    }

    override fun completeTask(completedTaskBean: TaskBean) {
        completeTask(completedTaskBean.id)
    }

    override fun completeTask(taskId: String) {
        dao.updateCompleted(taskId, true)
    }

    override fun activateTask(activeTaskBean: TaskBean) {
        activateTask(activeTaskBean.id)
    }

    override fun activateTask(taskId: String) {
        dao.updateCompleted(taskId, false)
    }

    override fun deleteTask(taskId: String) {
        dao.deleteTaskById(taskId)
    }

    override fun deleteAllTasks() {
        dao.deleteTasks()
    }

    companion object {

        @Volatile
        private var INSTANCE: TasksSqliteDataSource? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param context the context
         * @return the [TasksSqliteDataSource] instance
         */
        fun getInstance(context: Context): TasksSqliteDataSource = INSTANCE
                ?: synchronized(TasksSqliteDataSource::class.java) {
                    INSTANCE ?: TasksSqliteDataSource(context).also { INSTANCE = it }
                }

        @VisibleForTesting
        @JvmStatic
        internal fun clearInstance() {
            INSTANCE = null
        }
    }
}