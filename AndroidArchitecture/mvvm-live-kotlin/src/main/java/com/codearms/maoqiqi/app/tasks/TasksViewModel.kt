package com.codearms.maoqiqi.app.tasks

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import java.util.*

/**
 * Exposes the data to be used in the tasks screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 11:42
 */
class TasksViewModel(
        private val tasksRepository: TasksRepository
) : ViewModel() {

    private var currentFiltering = TasksFilterType.ALL_TASKS
    private var firstLoad = true

    // These observable fields will update Views automatically
    val observableLoading = MutableLiveData<Boolean>()
    val observableFilteringLabel = MutableLiveData<Int>()
    val observableList = MutableLiveData<List<TaskBean>>()
    val observableNoTasks = MutableLiveData<Boolean>()
    val observableNoTasksRes = MutableLiveData<Int>()
    val observableNoTasksText = MutableLiveData<Int>()

    internal val taskItemEvent = MutableLiveData<Event<String>>()
    internal val message = MutableLiveData<Event<String>>()

    init {
        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    internal fun start() {
        loadTasks(false)
    }

    public fun loadTasks(forceUpdate: Boolean) {
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
        if (showLoading) observableLoading.value = true

        // Notice:Tests are used to clear the data each time.
        // if (forceUpdate) tasksRepository.refreshTasks();

        tasksRepository.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                val tasksToShow = ArrayList<TaskBean>()

                // We filter the tasks based on the requestType
                for (taskBean in taskBeanList) {
                    when (currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(taskBean)
                        TasksFilterType.ACTIVE_TASKS -> if (taskBean.isActive) tasksToShow.add(taskBean)
                        TasksFilterType.COMPLETED_TASKS -> if (taskBean.isCompleted) tasksToShow.add(taskBean)
                    }
                }
                if (showLoading) observableLoading.value = false

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                if (showLoading) observableLoading.value = false
                // Show a message indicating there are no tasks for that filter type.
                observableNoTasks.value = true
            }
        })
    }

    private fun processTasks(taskBeanList: List<TaskBean>) {
        if (taskBeanList.isEmpty()) {
            observableNoTasks.setValue(true)
        } else {
            observableNoTasks.value = false
            observableList.setValue(taskBeanList)
        }
    }

    internal fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        message.value = Event(MessageMap.CLEAR)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    fun completeTask(completedTaskBean: TaskBean) {
        tasksRepository.completeTask(completedTaskBean)
        message.value = Event(MessageMap.COMPLETE)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    fun activateTask(activeTaskBean: TaskBean) {
        tasksRepository.activateTask(activeTaskBean)
        message.value = Event(MessageMap.ACTIVE)
        loadTasks(forceUpdate = false, showLoading = false)
    }

    fun openTaskDetails(requestedTaskBean: TaskBean) {
        taskItemEvent.value = Event(requestedTaskBean.id)
    }

    internal fun result(requestCode: Int, resultCode: Int) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            when (resultCode) {
                TasksActivity.RESULT_ADD_TASK -> message.setValue(Event(MessageMap.ADD))
                TasksActivity.RESULT_EDIT_TASK -> message.setValue(Event(MessageMap.EDIT))
                TasksActivity.RESULT_DELETE_TASK -> message.setValue(Event(MessageMap.DELETE))
            }
        }
    }

    /**
     * Sets the current task filtering type.
     */
    internal fun setFiltering(requestType: TasksFilterType) {
        currentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (currentFiltering) {
            TasksFilterType.ALL_TASKS -> {
                observableFilteringLabel.value = R.string.all_tasks
                observableNoTasksRes.value = R.drawable.ic_all_tasks
                observableNoTasksText.setValue(R.string.no_all_tasks)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                observableFilteringLabel.value = R.string.active_tasks
                observableNoTasksRes.value = R.drawable.ic_active_tasks
                observableNoTasksText.setValue(R.string.no_active_tasks)
            }
            TasksFilterType.COMPLETED_TASKS -> {
                observableFilteringLabel.value = R.string.completed_tasks
                observableNoTasksRes.value = R.drawable.ic_completed_tasks
                observableNoTasksText.setValue(R.string.no_completed_tasks)
            }
        }
    }
}