package com.codearms.maoqiqi.app.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.codearms.maoqiqi.app.LiveDataTestUtils
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

/**
 * Unit tests for the implementation of [StatisticsViewModel]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
class StatisticsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tasksRepository: TasksRepository

    @Captor
    private lateinit var loadTasksCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.LoadTasksCallBack>

    private lateinit var statisticsViewModel: StatisticsViewModel

    private lateinit var taskBeanList: MutableList<TaskBean>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        statisticsViewModel = StatisticsViewModel(tasksRepository)

        taskBeanList = ArrayList()
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, true))
    }

    @Test
    fun loadStatisticsEmpty() {
        // Given an initialized StatisticsViewModel with no tasks
        taskBeanList.clear()

        statisticsViewModel.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        assertThat(statisticsViewModel.observableActiveTasks.value, `is`(0))
        assertThat(statisticsViewModel.observableCompletedTasks.value, `is`(0))
    }

    @Test
    fun loadStatistics() {
        statisticsViewModel.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        assertThat(statisticsViewModel.observableActiveTasks.value, `is`(2))
        assertThat(statisticsViewModel.observableCompletedTasks.value, `is`(1))
    }

    @Test
    @Throws(InterruptedException::class)
    fun loadStatisticsError() {
        statisticsViewModel.start()

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        loadTasksCallBackArgumentCaptor.value.onDataNotAvailable()

        val event = LiveDataTestUtils.getValue(statisticsViewModel.message)
        assertEquals(event.getContent(), MessageMap.NO_DATA)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}