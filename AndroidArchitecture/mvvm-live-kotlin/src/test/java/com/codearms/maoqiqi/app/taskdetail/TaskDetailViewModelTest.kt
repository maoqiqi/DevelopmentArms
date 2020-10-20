package com.codearms.maoqiqi.app.taskdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.codearms.maoqiqi.app.LiveDataTestUtils
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TaskDataSource
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Unit tests for the implementation of [TaskDetailViewModel]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
class TaskDetailViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tasksRepository: TaskRepository

    @Captor
    private lateinit var getTaskCallBackArgumentCaptor: ArgumentCaptor<TaskDataSource.GetTaskCallBack>

    private lateinit var taskDetailViewModel: TaskDetailViewModel

    private lateinit var activeTaskBean: TaskBean
    private lateinit var completedTaskBean: TaskBean
    private lateinit var unknownTaskBean: TaskBean

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        taskDetailViewModel = TaskDetailViewModel(tasksRepository)

        activeTaskBean = TaskBean(TITLE, DESCRIPTION, false)
        completedTaskBean = TaskBean(TITLE, DESCRIPTION, true)
        unknownTaskBean = TaskBean("", TITLE, DESCRIPTION, false)
    }

    @Test
    fun getActiveTask() {
        // When tasks presenter is asked to open a task
        taskDetailViewModel.setTaskId(activeTaskBean.id)
        taskDetailViewModel.start()

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        // Trigger callback
        getTaskCallBackArgumentCaptor.value.onTaskLoaded(activeTaskBean)

        // Then verify that the view was notified
        assertEquals(taskDetailViewModel.observableTitle.value, activeTaskBean.title)
        assertEquals(taskDetailViewModel.observableDescription.value, activeTaskBean.description)
    }

    @Test
    fun getCompletedTask() {
        taskDetailViewModel.setTaskId(completedTaskBean.id)
        taskDetailViewModel.start()

        verify(tasksRepository).getTask(Matchers.eq(completedTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        getTaskCallBackArgumentCaptor.value.onTaskLoaded(completedTaskBean)

        assertEquals(taskDetailViewModel.observableTitle.value, completedTaskBean.title)
        assertEquals(taskDetailViewModel.observableDescription.value, completedTaskBean.description)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getUnknownTask() {
        // When loading of a task is requested with an invalid task ID.
        taskDetailViewModel.setTaskId(unknownTaskBean.id)
        taskDetailViewModel.start()
        val event = LiveDataTestUtils.getValue(taskDetailViewModel.message)
        assertEquals(event.getContent(), MessageMap.NO_ID)
    }

    @Test
    @Throws(InterruptedException::class)
    fun getTaskNotAvailable() {
        taskDetailViewModel.setTaskId(activeTaskBean.id)
        taskDetailViewModel.start()

        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        getTaskCallBackArgumentCaptor.value.onDataNotAvailable()

        val event = LiveDataTestUtils.getValue(taskDetailViewModel.message)
        assertEquals(event.getContent(), MessageMap.NO_DATA)
    }

    @Test
    fun completeTask() {
        // Given an initialized presenter with an active task
        taskDetailViewModel.setTaskId(activeTaskBean.id)

        // When the presenter is asked to complete the task
        taskDetailViewModel.completeTask()

        // Then a request is sent to the task repository and the UI is updated
        verify(tasksRepository).completeTask(activeTaskBean.id)
    }

    @Test
    fun activateTask() {
        // Given an initialized presenter with a completed task
        taskDetailViewModel.setTaskId(completedTaskBean.id)

        // When the presenter is asked to activate the task
        taskDetailViewModel.activateTask()

        verify(tasksRepository).activateTask(completedTaskBean.id)
    }

    @Test
    fun deleteTask() {
        // When the deletion of a task is requested
        taskDetailViewModel.setTaskId(activeTaskBean.id)

        taskDetailViewModel.deleteTask()

        // Then the repository and the view are notified
        verify(tasksRepository).deleteTask(activeTaskBean.id)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}