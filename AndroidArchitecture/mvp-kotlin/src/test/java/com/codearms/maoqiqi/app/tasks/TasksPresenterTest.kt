package com.codearms.maoqiqi.app.tasks

import com.codearms.maoqiqi.app.capture
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.util.*

/**
 * Unit tests for the implementation of [TasksPresenter]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
class TasksPresenterTest {

    @Mock
    private lateinit var tasksRepository: TasksRepository
    @Mock
    private lateinit var tasksView: TasksContract.View

    @Captor
    private lateinit var loadTasksCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.LoadTasksCallBack>
    @Captor
    private lateinit var listArgumentCaptor: ArgumentCaptor<List<TaskBean>>

    private lateinit var tasksPresenter: TasksPresenter

    private lateinit var taskBeanList: MutableList<TaskBean>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        // The presenter won't update the view unless it's active.
        `when`(tasksView.isActive).thenReturn(true)

        tasksPresenter = TasksPresenter(tasksRepository, tasksView)

        taskBeanList = ArrayList()
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, false))
        taskBeanList.add(TaskBean(TITLE, DESCRIPTION, true))
    }

    @Test
    fun setPresenter() {
        // The presenter is set to the view
        verify(tasksView).presenter = tasksPresenter
    }

    @Test
    fun loadTasks() {
        // When loading of Tasks is requested
        tasksPresenter.filtering = TasksFilterType.ALL_TASKS
        tasksPresenter.loadTasks(true)

        // Callback is captured and invoked with stubbed tasks
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())

        // Then progress indicator is shown
        val inOrder = Mockito.inOrder(tasksView)
        inOrder.verify(tasksView).setLoadingIndicator(true)

        // When task is finally loaded
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        // Then progress indicator is hidden and all tasks are shown in UI
        inOrder.verify(tasksView).setLoadingIndicator(false)
        verify(tasksView).showTasks(capture(listArgumentCaptor))
        assertEquals(listArgumentCaptor.value.size, 3)
    }

    @Test
    fun loadActiveTasks() {
        tasksPresenter.filtering = TasksFilterType.ACTIVE_TASKS
        tasksPresenter.loadTasks(true)

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        verify(tasksView).setLoadingIndicator(false)
        verify(tasksView).showTasks(capture(listArgumentCaptor))
        assertEquals(listArgumentCaptor.value.size, 2)
    }

    @Test
    fun loadCompletedTasks() {
        tasksPresenter.filtering = TasksFilterType.COMPLETED_TASKS
        tasksPresenter.loadTasks(true)

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        verify(tasksView).setLoadingIndicator(false)
        verify(tasksView).showTasks(capture(listArgumentCaptor))
        assertEquals(listArgumentCaptor.value.size, 1)
    }

    @Test
    fun showNoTasks() {
        tasksPresenter.filtering = TasksFilterType.ALL_TASKS
        tasksPresenter.loadTasks(true)

        // And the tasks aren't available in the repository
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture())
        loadTasksCallBackArgumentCaptor.value.onDataNotAvailable()

        // Then an error message is shown
        verify(tasksView).showNoTasks()
    }

    @Test
    fun clearCompletedTasks() {
        // When completed tasks are cleared
        tasksPresenter.clearCompletedTasks()

        // Then repository is called and the view is notified
        verify(tasksRepository).clearCompletedTasks()
        verify(tasksRepository).loadTasks(Mockito.any(TasksDataSource.LoadTasksCallBack::class.java))
    }

    @Test
    fun completeTask() {
        // Given a stubbed activated task
        val completedTaskBean = TaskBean(TITLE, DESCRIPTION, false)

        // When task is marked as complete
        tasksPresenter.completeTask(completedTaskBean)

        // Then repository is called and task marked complete UI is shown
        verify(tasksRepository).completeTask(completedTaskBean)
        verify(tasksView).showMessage(MessageMap.COMPLETE)
    }

    @Test
    fun activateTask() {
        // Given a stubbed completed task
        val activeTaskBean = TaskBean(TITLE, DESCRIPTION, true)

        // When task is marked as activated
        tasksPresenter.activateTask(activeTaskBean)

        verify(tasksRepository).activateTask(activeTaskBean)
        verify(tasksView).showMessage(MessageMap.ACTIVE)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}