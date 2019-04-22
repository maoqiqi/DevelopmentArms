package com.codearms.maoqiqi.app.tasks

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import java.util.*

/**
 * Listens to user actions from the UI ([TasksFragment]), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
class TasksPresenter internal constructor(
        private val tasksRepository: TasksRepository,
        private val tasksView: TasksContract.View
) : TasksContract.Presenter {

    override var filtering = TasksFilterType.ALL_TASKS

    private var firstLoad = true

    init {
        this.tasksView.presenter = this
    }

    override fun start() {
        loadTasks(false)
    }

    override fun loadTasks(forceUpdate: Boolean) {
        // The first load forcing update
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    /**
     * load tasks
     *
     * @param forceUpdate Pass in true to refresh the data in the [TasksDataSource]
     * @param showLoading Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoading: Boolean) {
        if (showLoading) tasksView.setLoadingIndicator(true)

        // Notice:Tests are used to clear the data each time.
        // if (forceUpdate) tasksRepository.refreshTasks()

        tasksRepository.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                val tasksToShow = ArrayList<TaskBean>()

                // We filter the tasks based on the requestType
                for (taskBean in taskBeanList) {
                    when (filtering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(taskBean)
                        TasksFilterType.ACTIVE_TASKS -> if (taskBean.isActive) tasksToShow.add(taskBean)
                        TasksFilterType.COMPLETED_TASKS -> if (taskBean.isCompleted) tasksToShow.add(taskBean)
                    }
                }

                // The view may not be able to handle UI updates anymore
                if (!tasksView.isActive) return

                if (showLoading) tasksView.setLoadingIndicator(false)

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                if (!tasksView.isActive) return

                if (showLoading) tasksView.setLoadingIndicator(false)
                tasksView.showNoTasks()
            }
        })
    }

    private fun processTasks(taskBeanList: List<TaskBean>) {
        if (taskBeanList.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            tasksView.showNoTasks()
        } else {
            // Set the filter label's text.
            tasksView.showFilterLabel()
            // Show the list of tasks
            tasksView.showTasks(taskBeanList)
        }
    }

    override fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        tasksView.showMessage(MessageMap.CLEAR)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    override fun completeTask(completedTaskBean: TaskBean) {
        tasksRepository.completeTask(completedTaskBean)
        tasksView.showMessage(MessageMap.COMPLETE)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    override fun activateTask(activeTaskBean: TaskBean) {
        tasksRepository.activateTask(activeTaskBean)
        tasksView.showMessage(MessageMap.ACTIVE)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            when (resultCode) {
                TasksActivity.RESULT_ADD_TASK -> tasksView.showMessage(MessageMap.ADD)
                TasksActivity.RESULT_EDIT_TASK -> tasksView.showMessage(MessageMap.EDIT)
                TasksActivity.RESULT_DELETE_TASK -> tasksView.showMessage(MessageMap.DELETE)
            }
        }
    }
}