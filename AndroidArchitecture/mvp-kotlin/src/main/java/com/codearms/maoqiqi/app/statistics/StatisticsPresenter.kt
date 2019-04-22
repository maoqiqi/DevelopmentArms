package com.codearms.maoqiqi.app.statistics

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap

/**
 * Listens to user actions from the UI ([StatisticsFragment]), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
class StatisticsPresenter internal constructor(
        private val tasksRepository: TasksRepository,
        private val statisticsView: StatisticsContract.View
) : StatisticsContract.Presenter {

    init {
        this.statisticsView.presenter = this
    }

    override fun start() {
        loadStatistics()
    }

    override fun loadStatistics() {
        tasksRepository.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                // We calculate number of active and completed tasks
                val activeTasks = taskBeanList.filter { it.isActive }.size
                val completedTasks = taskBeanList.size - activeTasks

                if (statisticsView.isActive)
                    statisticsView.showStatistics(activeTasks, completedTasks)
            }

            override fun onDataNotAvailable() {
                if (statisticsView.isActive) statisticsView.showMessage(MessageMap.NO_DATA)
            }
        })
    }
}