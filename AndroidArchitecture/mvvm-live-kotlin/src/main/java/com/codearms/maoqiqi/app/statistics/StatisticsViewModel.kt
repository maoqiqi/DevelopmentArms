package com.codearms.maoqiqi.app.statistics

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

import com.codearms.maoqiqi.app.Event
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap

/**
 * Exposes the data to be used in the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 13:28
 */
class StatisticsViewModel(
        private val tasksRepository: TasksRepository
) : ViewModel() {

    val observableActiveTasks = MutableLiveData<Int>()
    val observableCompletedTasks = MutableLiveData<Int>()

    internal val message = MutableLiveData<Event<String>>()

    internal fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        tasksRepository.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                var activeTasks = 0
                var completedTasks = 0

                // We calculate number of active and completed tasks
                for (taskBean in taskBeanList) {
                    if (taskBean.isCompleted) {
                        completedTasks += 1
                    } else {
                        activeTasks += 1
                    }
                }

                observableActiveTasks.value = activeTasks
                observableCompletedTasks.value = completedTasks
            }

            override fun onDataNotAvailable() {
                message.value = Event(MessageMap.NO_DATA)
            }
        })
    }
}