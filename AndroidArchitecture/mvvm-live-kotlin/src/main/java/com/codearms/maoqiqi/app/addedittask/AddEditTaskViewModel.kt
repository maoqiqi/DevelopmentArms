package com.codearms.maoqiqi.app.addedittask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.data.Result
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 18:18
 */
class AddEditTaskViewModel(
    private val tasksRepository: TaskRepository
) : ViewModel() {

    private var taskId: String? = null

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private var isDataLoaded = false

    internal val addTaskEvent = MutableLiveData<Event<Any>>()
    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    internal fun start(taskId: String?) {
        this.taskId = taskId

        if (taskId == null || isDataLoaded) {
            return
        }

        viewModelScope.launch {
            val result = tasksRepository.getTask(taskId)
            if (result is Result.Success) {
                val taskBean = result.data
                title.value = taskBean.title
                description.value = taskBean.description
                isDataLoaded = false
            } else {
                _message.value = Event(MessageMap.NO_DATA)
            }
        }
    }

    // Called when clicking on fab.
    internal fun addTask() {
        val currentTitle: String? = title.value
        val currentDescription: String? = description.value

        if (currentTitle == null || currentTitle == "" || currentDescription == null || currentDescription == "") {
            _message.value = Event(MessageMap.ENTER)
            return
        }

        viewModelScope.launch {
            if (isNewTask()) {
                tasksRepository.addTask(TaskBean(title = currentTitle, description = currentDescription))
            } else {
                tasksRepository.updateTask(TaskBean(taskId!!, currentTitle, currentDescription, false))
            }

            // After an add or edit, go back to the list.
            addTaskEvent.value = Event(Any())
        }
    }

    private fun isNewTask(): Boolean {
        return taskId == null
    }
}