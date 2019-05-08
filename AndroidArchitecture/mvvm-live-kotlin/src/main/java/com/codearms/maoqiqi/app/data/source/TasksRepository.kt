package com.codearms.maoqiqi.app.data.source

import android.support.annotation.VisibleForTesting
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource
import java.util.*

/**
 * Load tasks from the data sources into a cache.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 16:20
 */
class TasksRepository private constructor(
        private val tasksRemoteDataSource: TasksDataSource,
        private val tasksLocalDataSource: TasksDataSource
) : TasksDataSource {

    internal var cachedTasksMap: MutableMap<String, TaskBean> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    private var cacheIsDirty = false

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is available first.
     */
    override fun loadTasks(callBack: TasksDataSource.LoadTasksCallBack) {
        // Respond immediately with cache if available and not dirty
        if (cachedTasksMap.isNotEmpty() && !cacheIsDirty) {
            callBack.onTasksLoaded(ArrayList(cachedTasksMap.values))
            return
        }

        EspressoIdlingResource.increment()

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callBack)
        } else {
            // Query the local storage if available. If not, query the network.
            tasksLocalDataSource.loadTasks(object : TasksDataSource.LoadTasksCallBack {
                override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                    refreshCache(taskBeanList)
                    if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                        EspressoIdlingResource.decrement()
                    }
                    callBack.onTasksLoaded(ArrayList(cachedTasksMap.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callBack)
                }
            })
        }
    }

    override fun getTask(taskId: String, callBack: TasksDataSource.GetTaskCallBack) {
        val cachedTaskBean = getTaskById(taskId)
        // Respond immediately with cache if available
        if (cachedTaskBean != null) {
            callBack.onTaskLoaded(cachedTaskBean)
            return
        }

        EspressoIdlingResource.increment()

        // Load from server/persisted if needed.

        // Query the local storage if available. If not, query the network.
        tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                refreshCache(taskBean)
                if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }
                callBack.onTaskLoaded(taskBean)
            }

            override fun onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallBack {
                    override fun onTaskLoaded(taskBean: TaskBean) {
                        refreshCache(taskBean)
                        tasksLocalDataSource.addTask(taskBean)
                        if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                            EspressoIdlingResource.decrement()
                        }
                        callBack.onTaskLoaded(taskBean)
                    }

                    override fun onDataNotAvailable() {
                        if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                            EspressoIdlingResource.decrement()
                        }
                        callBack.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        cachedTasksMap = cachedTasksMap.filterValues { it.isActive } as LinkedHashMap
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun addTask(taskBean: TaskBean) {
        tasksRemoteDataSource.addTask(taskBean)
        tasksLocalDataSource.addTask(taskBean)
        refreshCache(taskBean)
    }

    override fun updateTask(taskBean: TaskBean) {
        tasksRemoteDataSource.updateTask(taskBean)
        tasksLocalDataSource.updateTask(taskBean)
        refreshCache(taskBean)
    }

    override fun completeTask(completedTaskBean: TaskBean) {
        tasksRemoteDataSource.completeTask(completedTaskBean)
        tasksLocalDataSource.completeTask(completedTaskBean)

        completedTaskBean.isCompleted = true
        refreshCache(completedTaskBean)
    }

    override fun completeTask(taskId: String) {
        getTaskById(taskId)?.let { completeTask(it) }
    }

    override fun activateTask(activeTaskBean: TaskBean) {
        tasksRemoteDataSource.activateTask(activeTaskBean)
        tasksLocalDataSource.activateTask(activeTaskBean)

        activeTaskBean.isCompleted = false
        refreshCache(activeTaskBean)
    }

    override fun activateTask(taskId: String) {
        getTaskById(taskId)?.let { activateTask(it) }
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)
        cachedTasksMap.remove(taskId)
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasksMap.clear()
    }

    /**
     * Getting new data from the network.
     */
    private fun getTasksFromRemoteDataSource(callBack: TasksDataSource.LoadTasksCallBack) {
        tasksRemoteDataSource.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                refreshCache(taskBeanList)
                refreshLocalDataSource(taskBeanList)
                if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }
                callBack.onTasksLoaded(ArrayList(cachedTasksMap.values))
            }

            override fun onDataNotAvailable() {
                if (!EspressoIdlingResource.countingIdlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }
                callBack.onDataNotAvailable()
            }
        })
    }

    /**
     * Refresh cache data
     */
    private fun refreshCache(taskBeanList: List<TaskBean>) {
        cachedTasksMap.clear()
        taskBeanList.forEach { cachedTasksMap[it.id] = it }
        cacheIsDirty = false
    }

    /**
     * Refresh cache data
     */
    private fun refreshCache(taskBean: TaskBean) {
        // Do in memory cache update to keep the app UI up to date
        cachedTasksMap[taskBean.id] = taskBean
    }

    /**
     * Refresh local source data
     */
    private fun refreshLocalDataSource(taskBeanList: List<TaskBean>) {
        tasksLocalDataSource.deleteAllTasks()
        taskBeanList.forEach { tasksLocalDataSource.addTask(it) }
    }

    /**
     * Getting a task by id
     */
    @VisibleForTesting
    internal fun getTaskById(taskId: String?): TaskBean? = cachedTasksMap[taskId]

    companion object {

        @Volatile
        private var INSTANCE: TasksRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param tasksRemoteDataSource the backend data source
         * @param tasksLocalDataSource  the device storage data source
         * @return the [TasksRepository] instance
         */
        @JvmStatic
        fun getInstance(tasksRemoteDataSource: TasksDataSource, tasksLocalDataSource: TasksDataSource): TasksRepository = INSTANCE
                ?: synchronized(TasksRepository::class.java) {
                    INSTANCE
                            ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource).also { INSTANCE = it }
                }

        /**
         * On the next call, force [.getInstance] to create a new instance.
         */
        @VisibleForTesting
        @JvmStatic
        internal fun destroyInstance() {
            INSTANCE = null
        }
    }
}