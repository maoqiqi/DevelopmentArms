package com.codearms.maoqiqi.app.data.source

import com.codearms.maoqiqi.app.data.Result

import com.codearms.maoqiqi.app.data.TaskBean
import kotlinx.coroutines.*
import java.util.*

/**
 * Load tasks from the data sources into a cache.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 16:20
 */
class TaskRepository(
    private val taskRemoteDataSource: TaskDataSource,
    private val taskLocalDataSource: TaskDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {

    internal var cachedTasksMap: MutableMap<String, TaskBean> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    private var cacheIsDirty = false

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is available first.
     */
    override suspend fun loadTasks(): Result<List<TaskBean>> {
        // Query the local storage if available. If not, query the network.
        if (cacheIsDirty) {
            try {
                getTasksFromRemoteDataSource()
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return taskLocalDataSource.loadTasks()
    }

    override suspend fun getTask(taskId: String): Result<TaskBean> {
        val localTask = taskLocalDataSource.getTask(taskId)
        if (localTask is Result.Success) {
            return localTask
        }

        val remoteTask = taskRemoteDataSource.getTask(taskId)
        if (remoteTask is Result.Success) {
            taskLocalDataSource.updateTask(remoteTask.data)
        }

        return remoteTask
    }

    override suspend fun clearCompletedTasks() {
        taskRemoteDataSource.clearCompletedTasks()
        taskLocalDataSource.clearCompletedTasks()
    }

    override suspend fun refreshTasks() {
        cacheIsDirty = true
    }

    override suspend fun addTask(taskBean: TaskBean) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.addTask(taskBean) }
                launch { taskLocalDataSource.addTask(taskBean) }
            }
        }
    }

    override suspend fun updateTask(taskBean: TaskBean) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.updateTask(taskBean) }
                launch { taskLocalDataSource.updateTask(taskBean) }
            }
        }
    }

    override suspend fun completeTask(completedTaskBean: TaskBean) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.completeTask(completedTaskBean) }
                launch { taskLocalDataSource.completeTask(completedTaskBean) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskById(taskId) as? Result.Success)?.let {
                completeTask(it.data)
            }
        }
    }

    override suspend fun activateTask(activeTaskBean: TaskBean) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.activateTask(activeTaskBean) }
                launch { taskLocalDataSource.activateTask(activeTaskBean) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskById(taskId) as? Result.Success)?.let {
                activateTask(it.data)
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.deleteTask(taskId) }
                launch { taskLocalDataSource.deleteTask(taskId) }
            }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.deleteAllTasks() }
                launch { taskLocalDataSource.deleteAllTasks() }
            }
        }
    }

    /**
     * Getting new data from the network.
     */
    private suspend fun getTasksFromRemoteDataSource() {
        val remoteTasks = taskRemoteDataSource.loadTasks()

        if (remoteTasks is Result.Success) {
            // Real apps might want to do a proper sync, deleting, modifying or adding each task.
            taskLocalDataSource.deleteAllTasks()
            remoteTasks.data.forEach { task -> taskLocalDataSource.addTask(task) }
            cacheIsDirty = false
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    private suspend fun getTaskById(taskId: String): Result<TaskBean> = taskLocalDataSource.getTask(taskId)
}