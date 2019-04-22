package com.codearms.maoqiqi.app.addedittask

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap

/**
 * Listens to user actions from the UI ([AddEditTaskFragment]), retrieves the data and updates the UI as required.
 * @param taskId          ID of the task to edit or null for a new task
 * @param tasksRepository a repository of data for tasks
 * @param addEditTaskView the add/edit view
 * @param isDataMissing   whether data needs to be loaded or not (for config changes)
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
class AddEditTaskPresenter internal constructor(
        private val taskId: String?,
        private val tasksRepository: TasksRepository,
        private val addEditTaskView: AddEditTaskContract.View,
        private var isDataMissing: Boolean
) : AddEditTaskContract.Presenter {

    init {
        this.addEditTaskView.presenter = this
    }

    override fun start() {
        if (!isNewTask() && isDataMissing) getTask()
    }

    override fun getTask() {
        if (isNewTask()) throw RuntimeException("getTask() was called but task is new.")

        tasksRepository.getTask(taskId!!, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                if (addEditTaskView.isActive) addEditTaskView.setData(taskBean)
                isDataMissing = false
            }

            override fun onDataNotAvailable() {
                if (addEditTaskView.isActive) addEditTaskView.showMessage(MessageMap.NO_DATA)
            }
        })
    }

    override fun isDataMissing(): Boolean {
        return isDataMissing
    }

    // Called when clicking on fab.
    override fun addTask(title: String, description: String) {
        if (title == "" || description == "") {
            addEditTaskView.showMessage(MessageMap.ENTER)
            return
        }

        val taskBean: TaskBean
        if (isNewTask()) {
            taskBean = TaskBean(title, description, false)
            tasksRepository.addTask(taskBean)
        } else {
            taskBean = TaskBean(taskId!!, title, description, false)
            tasksRepository.updateTask(taskBean)
        }
        // After an add or edit, go back to the list.
        addEditTaskView.showTasks()
    }

    private fun isNewTask(): Boolean {
        return taskId == null || taskId == ""
    }
}