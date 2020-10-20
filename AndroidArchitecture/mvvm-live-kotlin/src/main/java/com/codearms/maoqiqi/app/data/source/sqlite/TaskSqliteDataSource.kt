package com.codearms.maoqiqi.app.data.source.sqlite

import com.codearms.maoqiqi.app.data.Result

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TaskDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * The sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
class TaskSqliteDataSource private constructor(
    private val dao: TaskDAO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {


    override suspend fun loadTasks(): Result<List<TaskBean>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(dao.loadTasks())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<TaskBean> = withContext(ioDispatcher) {
        return@withContext try {
            val taskBean: TaskBean? = dao.getTaskById(taskId)
            if (taskBean != null) {
                return@withContext Result.Success(taskBean)
            } else {
                return@withContext Result.Error(Exception("Task not found!"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearCompletedTasks() {
        dao.deleteCompletedTasks()
    }

    override suspend fun refreshTasks() {

    }

    override suspend fun addTask(taskBean: TaskBean) = withContext(ioDispatcher) {
        dao.addTask(taskBean)
    }

    override suspend fun updateTask(taskBean: TaskBean) = withContext(ioDispatcher) {
        dao.updateTask(taskBean)
    }

    override suspend fun completeTask(completedTaskBean: TaskBean) = withContext(ioDispatcher) {
        completeTask(completedTaskBean.id)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        dao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(activeTaskBean: TaskBean) = withContext(ioDispatcher) {
        activateTask(activeTaskBean.id)
    }

    override suspend fun activateTask(taskId: String) = withContext(ioDispatcher) {
        dao.updateCompleted(taskId, false)
    }

    override suspend fun deleteTask(taskId: String) = withContext(ioDispatcher) {
        dao.deleteTaskById(taskId)
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        dao.deleteTasks()
    }
}