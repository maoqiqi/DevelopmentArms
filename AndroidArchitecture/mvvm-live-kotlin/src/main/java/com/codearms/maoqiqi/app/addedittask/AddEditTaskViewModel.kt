package com.codearms.maoqiqi.app.addedittask

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap

/**
 * Exposes the data to be used in the add task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 18:18
 */
class AddEditTaskViewModel(
        private val tasksRepository: TasksRepository
) : ViewModel() {

    private var taskId: String? = null

    // Two-way databinding, exposing MutableLiveData
    val observableTitle = MutableLiveData<String>()
    // Two-way databinding, exposing MutableLiveData
    val observableDescription = MutableLiveData<String>()

    internal var isDataMissing = true

    internal val addTaskEvent = MutableLiveData<Event<Any>>()
    internal val message = MutableLiveData<Event<String>>()

    internal fun setTaskId(taskId: String?) {
        this.taskId = taskId
    }

    internal fun start() {
        if (!isNewTask() && isDataMissing) getTask()
    }

    private fun getTask() {
        if (isNewTask()) throw RuntimeException("getTask() was called but task is new.")

        tasksRepository.getTask(taskId!!, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                observableTitle.value = taskBean.title
                observableDescription.value = taskBean.description
                isDataMissing = false
            }

            override fun onDataNotAvailable() {
                message.value = Event(MessageMap.NO_DATA)
            }
        })
    }

    // Called when clicking on fab.
    internal fun addTask() {
        if (observableTitle.value == null || "" == observableTitle.value ||
                observableDescription.value == null || "" == observableDescription.value) {
            message.value = Event(MessageMap.ENTER)
            return
        }

        val taskBean: TaskBean
        if (isNewTask()) {
            taskBean = TaskBean(observableTitle.value!!, observableDescription.value!!, false)
            tasksRepository.addTask(taskBean)
        } else {
            taskBean = TaskBean(taskId!!, observableTitle.value!!, observableDescription.value!!, false)
            tasksRepository.updateTask(taskBean)
        }

        // After an add or edit, go back to the list.
        addTaskEvent.value = Event(Any())
    }

    private fun isNewTask(): Boolean {
        return taskId == null
    }
}