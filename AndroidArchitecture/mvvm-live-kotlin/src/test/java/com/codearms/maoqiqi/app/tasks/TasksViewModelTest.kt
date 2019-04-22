package com.codearms.maoqiqi.app.tasks

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.codearms.maoqiqi.app.LiveDataTestUtils
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify
import java.util.*

/**
 * Unit tests for the implementation of [TasksViewModel]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
class TasksViewModelTest {

    // Executes each task synchronously using Architecture Components.
    // testImplementation "android.arch.core:core-testing:$archLifecycleVersion"
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tasksRepository: TasksRepository

    @Captor
    private lateinit var loadTasksCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.LoadTasksCallBack>

    private lateinit var tasksViewModel: TasksViewModel

    private lateinit var taskBeanList: MutableList<TaskBean>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        tasksViewModel = TasksViewModel(tasksRepository)

        taskBeanList = ArrayList()
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, true))
    }

    @Test
    fun loadTasks() {
        // When loading of Tasks is requested
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
        tasksViewModel.loadTasks(true)

        // Callback is captured and invoked with stubbed tasks
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        // Then progress indicator is shown
        assertEquals(tasksViewModel.observableLoading.value, true)

        // When task is finally loaded
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        // Then progress indicator is hidden
        assertEquals(tasksViewModel.observableLoading.value, false)

        // And data loaded
        assertEquals(Objects.requireNonNull<List<TaskBean>>(tasksViewModel.observableList.value).size, 3)
    }

    @Test
    fun loadActiveTasks() {
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
        tasksViewModel.loadTasks(true)

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        assertEquals(tasksViewModel.observableLoading.value, false)
        assertEquals(Objects.requireNonNull<List<TaskBean>>(tasksViewModel.observableList.value).size, 2)
    }

    @Test
    fun loadCompletedTasks() {
        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
        tasksViewModel.loadTasks(true)

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        assertEquals(tasksViewModel.observableLoading.value, false)
        assertEquals(Objects.requireNonNull<List<TaskBean>>(tasksViewModel.observableList.value).size, 1)
    }

    @Test
    fun showNoTasks() {
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
        tasksViewModel.loadTasks(true)

        // And the tasks aren't available in the repository
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onDataNotAvailable()

        // Then an error message is shown
        assertEquals(tasksViewModel.observableNoTasks.value, true)
    }

    @Test
    fun clearCompletedTasks() {
        // When completed tasks are cleared
        tasksViewModel.clearCompletedTasks()

        // Then repository is called and the view is notified
        verify(tasksRepository).clearCompletedTasks()
        verify(tasksRepository).loadTasks(Mockito.any(TasksDataSource.LoadTasksCallBack::class.java))
    }

    @Test
    @Throws(InterruptedException::class)
    fun completeTask() {
        // Given a stubbed activated task
        val completedTaskBean = TaskBean(TITLE, DESCRIPTION, false)

        // When task is marked as complete
        tasksViewModel.completeTask(completedTaskBean)

        // Then repository is called and task marked complete UI is shown
        verify(tasksRepository).completeTask(completedTaskBean)

        val event = LiveDataTestUtils.getValue(tasksViewModel.message)
        assertEquals(event.getContent(), MessageMap.COMPLETE)
    }

    @Test
    @Throws(InterruptedException::class)
    fun activateTask() {
        // Given a stubbed completed task
        val activeTaskBean = TaskBean(TITLE, DESCRIPTION, true)

        // When task is marked as activated
        tasksViewModel.activateTask(activeTaskBean)

        // Then repository is called and task marked active UI is shown
        verify(tasksRepository).activateTask(activeTaskBean)

        val event = LiveDataTestUtils.getValue(tasksViewModel.message)
        assertEquals(event.getContent(), MessageMap.ACTIVE)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}