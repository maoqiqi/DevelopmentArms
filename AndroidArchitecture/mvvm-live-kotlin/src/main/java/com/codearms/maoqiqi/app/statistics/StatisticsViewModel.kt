package com.codearms.maoqiqi.app.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.data.Result
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import kotlinx.coroutines.launch

/**
 * Exposes the data to be used in the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 13:28
 */
class StatisticsViewModel(
    private val tasksRepository: TaskRepository
) : ViewModel() {

    val observableActiveTasks = MutableLiveData<Int>()
    val observableCompletedTasks = MutableLiveData<Int>()

    internal val message = MutableLiveData<Event<String>>()

    internal fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            val result = tasksRepository.loadTasks()
            if (result is Result.Success) {
                var activeTasks = 0
                var completedTasks = 0

                // We calculate number of active and completed tasks
                for (taskBean in result.data) {
                    if (taskBean.isCompleted) {
                        completedTasks += 1
                    } else {
                        activeTasks += 1
                    }
                }

                observableActiveTasks.value = activeTasks
                observableCompletedTasks.value = completedTasks
            } else {
                message.value = Event(MessageMap.NO_DATA)
            }
        }
    }
}