package com.codearms.maoqiqi.app.statistics

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify
import java.util.*

/**
 * Unit tests for the implementation of [StatisticsPresenter]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
class StatisticsPresenterTest {

    @Mock
    private lateinit var tasksRepository: TasksRepository
    @Mock
    private lateinit var statisticsView: StatisticsContract.View

    @Captor
    private lateinit var loadTasksCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.LoadTasksCallBack>

    private lateinit var statisticsPresenter: StatisticsPresenter

    private lateinit var taskBeanList: MutableList<TaskBean>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(statisticsView.isActive).thenReturn(true)

        statisticsPresenter = StatisticsPresenter(tasksRepository, statisticsView)

        taskBeanList = ArrayList()
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, true))
    }

    @Test
    fun setPresenter() {
        verify(statisticsView).presenter = statisticsPresenter
    }

    @Test
    fun loadStatisticsEmpty() {
        // Given an initialized StatisticsPresenter with no tasks
        taskBeanList.clear()

        statisticsPresenter.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        verify(statisticsView).showStatistics(0, 0)
    }

    @Test
    fun loadStatistics() {
        statisticsPresenter.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        verify(statisticsView).showStatistics(2, 1)
    }

    @Test
    fun loadStatisticsUnavailable() {
        statisticsPresenter.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onDataNotAvailable()

        verify(statisticsView).showMessage(MessageMap.NO_DATA)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}