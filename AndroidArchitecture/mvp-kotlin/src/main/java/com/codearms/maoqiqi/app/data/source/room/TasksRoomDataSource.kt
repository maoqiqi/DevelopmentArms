package com.codearms.maoqiqi.app.data.source.room

import android.content.Context
import androidx.annotation.VisibleForTesting

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.utils.AppExecutors

/**
 * Use room to create the sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
class TasksRoomDataSource private constructor(
        context: Context,
        private val appExecutors: AppExecutors
) : TasksDataSource {

    private val dao: TasksDAO = TaskDataBase.getInstance(context).tasksDAO()

    override fun loadTasks(callBack: TasksDataSource.LoadTasksCallBack) {
        appExecutors.diskIO.execute {
            val list = dao.loadTasks()
            appExecutors.mainThread.execute {
                if (list.isEmpty()) {
                    callBack.onDataNotAvailable()
                } else {
                    callBack.onTasksLoaded(list)
                }
            }
        }
    }

    override fun getTask(taskId: String, callBack: TasksDataSource.GetTaskCallBack) {
        appExecutors.diskIO.execute {
            val taskBean = dao.getTaskById(taskId)
            appExecutors.mainThread.execute {
                if (taskBean == null) {
                    callBack.onDataNotAvailable()
                } else {
                    callBack.onTaskLoaded(taskBean)
                }
            }
        }
    }

    override fun clearCompletedTasks() {
        appExecutors.diskIO.execute { dao.deleteCompletedTasks() }
    }

    override fun refreshTasks() {

    }

    override fun addTask(taskBean: TaskBean) {
        appExecutors.diskIO.execute { dao.addTask(taskBean) }
    }

    override fun updateTask(taskBean: TaskBean) {
        appExecutors.diskIO.execute { dao.updateTask(taskBean) }
    }

    override fun completeTask(completedTaskBean: TaskBean) {
        completeTask(completedTaskBean.id)
    }

    override fun completeTask(taskId: String) {
        appExecutors.diskIO.execute { dao.updateCompleted(taskId, true) }
    }

    override fun activateTask(activeTaskBean: TaskBean) {
        activateTask(activeTaskBean.id)
    }

    override fun activateTask(taskId: String) {
        appExecutors.diskIO.execute { dao.updateCompleted(taskId, false) }
    }

    override fun deleteTask(taskId: String) {
        appExecutors.diskIO.execute { dao.deleteTaskById(taskId) }
    }

    override fun deleteAllTasks() {
        appExecutors.diskIO.execute { dao.deleteTasks() }
    }

    companion object {
        @Volatile
        private var INSTANCE: TasksRoomDataSource? = null

        @JvmStatic
        fun getInstance(context: Context, appExecutors: AppExecutors): TasksRoomDataSource = INSTANCE
                ?: synchronized(TasksRoomDataSource::class.java) {
                    INSTANCE ?: TasksRoomDataSource(context, appExecutors).also { INSTANCE = it }
                }

        @VisibleForTesting
        @JvmStatic
        internal fun clearInstance() {
            INSTANCE = null
        }
    }
}