package com.codearms.maoqiqi.app.taskdetail

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap

/**
 * Listens to user actions from the UI ([TaskDetailFragment]), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
class TaskDetailPresenter internal constructor(
        private val taskId: String?,
        private val tasksRepository: TasksRepository,
        private val taskDetailView: TaskDetailContract.View
) : TaskDetailContract.Presenter {

    init {
        this.taskDetailView.presenter = this
    }

    override fun start() {
        getTask()
    }

    override fun getTask() {
        if (isInvalidTaskId()) return

        tasksRepository.getTask(taskId!!, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                if (taskDetailView.isActive) taskDetailView.showTask(taskBean)
            }

            override fun onDataNotAvailable() {
                if (taskDetailView.isActive) taskDetailView.showMessage(MessageMap.NO_DATA)
            }
        })
    }

    override fun completeTask() {
        if (isInvalidTaskId()) return
        tasksRepository.completeTask(taskId!!)
        taskDetailView.showMessage(MessageMap.COMPLETE)
    }

    override fun activateTask() {
        if (isInvalidTaskId()) return
        tasksRepository.activateTask(taskId!!)
        taskDetailView.showMessage(MessageMap.ACTIVE)
    }

    override fun deleteTask() {
        if (isInvalidTaskId()) return
        tasksRepository.deleteTask(taskId!!)
        taskDetailView.deleteTask()
    }

    private fun isInvalidTaskId(): Boolean {
        if (taskId == null || taskId == "") {
            taskDetailView.showMessage(MessageMap.NO_ID)
            return true
        }
        return false
    }
}