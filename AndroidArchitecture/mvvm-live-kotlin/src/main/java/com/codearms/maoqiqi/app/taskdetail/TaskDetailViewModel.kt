package com.codearms.maoqiqi.app.taskdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.data.Result
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import kotlinx.coroutines.launch

/**
 * Exposes the data to be used in the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 17:28
 */
class TaskDetailViewModel(
    private val tasksRepository: TaskRepository
) : ViewModel() {

    private var taskId: String? = null

    val observableTitle = MutableLiveData<String>()
    val observableDescription = MutableLiveData<String>()
    val observableCompleted = MutableLiveData<Boolean>()

    internal val taskDetailEvent = MutableLiveData<Event<Any>>()
    internal val message = MutableLiveData<Event<String>>()

    internal fun setTaskId(taskId: String?) {
        this.taskId = taskId
    }

    internal fun start() {
        getTask()
    }

    private fun getTask() {
        if (isInvalidTaskId()) return

        viewModelScope.launch {
            val result = tasksRepository.getTask(taskId!!)
            if (result is Result.Success) {
                val taskBean = result.data
                observableTitle.value = taskBean.title
                observableDescription.value = taskBean.description
                observableCompleted.value = taskBean.isCompleted
            } else {
                message.value = Event(MessageMap.NO_DATA)
            }
        }
    }

    fun completeTask() {
        if (isInvalidTaskId()) return
        viewModelScope.launch {
            tasksRepository.completeTask(taskId!!)
            observableCompleted.value = true
            message.value = Event(MessageMap.COMPLETE)
        }
    }

    fun activateTask() {
        if (isInvalidTaskId()) return
        viewModelScope.launch {
            tasksRepository.activateTask(taskId!!)
            observableCompleted.value = false
            message.value = Event(MessageMap.ACTIVE)
        }
    }

    internal fun deleteTask() {
        if (isInvalidTaskId()) return
        viewModelScope.launch {
            tasksRepository.deleteTask(taskId!!)
            taskDetailEvent.value = Event(Any())
        }
    }

    private fun isInvalidTaskId(): Boolean {
        if (taskId == null || taskId == "") {
            message.value = Event(MessageMap.NO_ID)
            return true
        }
        return false
    }
}