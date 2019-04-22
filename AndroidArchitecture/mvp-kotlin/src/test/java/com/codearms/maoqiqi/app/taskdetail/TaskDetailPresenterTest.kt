package com.codearms.maoqiqi.app.taskdetail

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Unit tests for the implementation of [TaskDetailPresenter]
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
class TaskDetailPresenterTest {

    @Mock
    private lateinit var tasksRepository: TasksRepository
    @Mock
    private lateinit var taskDetailView: TaskDetailContract.View

    @Captor
    private lateinit var getTaskCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.GetTaskCallBack>

    private lateinit var taskDetailPresenter: TaskDetailPresenter

    private lateinit var activeTaskBean: TaskBean
    private lateinit var completedTaskBean: TaskBean
    private lateinit var unknownTaskBean: TaskBean

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(taskDetailView.isActive).thenReturn(true)

        activeTaskBean = TaskBean(TITLE, DESCRIPTION, false)
        completedTaskBean = TaskBean(TITLE, DESCRIPTION, true)
        unknownTaskBean = TaskBean("", TITLE, DESCRIPTION, false)
    }

    @Test
    fun setPresenter() {
        taskDetailPresenter = TaskDetailPresenter(activeTaskBean.id, tasksRepository, taskDetailView)
        verify(taskDetailView).presenter = taskDetailPresenter
    }

    @Test
    fun getActiveTask() {
        // When tasks presenter is asked to open a task
        taskDetailPresenter = TaskDetailPresenter(activeTaskBean.id, tasksRepository, taskDetailView)
        taskDetailPresenter.start()

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        // Trigger callback
        getTaskCallBackArgumentCaptor.value.onTaskLoaded(activeTaskBean)

        // Title, description and completion status are shown in UI
        verify(taskDetailView).showTask(activeTaskBean)
    }

    @Test
    fun getCompletedTask() {
        taskDetailPresenter = TaskDetailPresenter(completedTaskBean.id, tasksRepository, taskDetailView)
        taskDetailPresenter.start()

        verify(tasksRepository).getTask(Matchers.eq(completedTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        getTaskCallBackArgumentCaptor.value.onTaskLoaded(completedTaskBean)

        verify(taskDetailView).showTask(completedTaskBean)
    }

    @Test
    fun getUnknownTask() {
        // When loading of a task is requested with an invalid task ID.
        taskDetailPresenter = TaskDetailPresenter(unknownTaskBean.id, tasksRepository, taskDetailView)
        taskDetailPresenter.start()
        verify(taskDetailView).showMessage(MessageMap.NO_ID)
    }

    @Test
    fun getTaskNotAvailable() {
        taskDetailPresenter = TaskDetailPresenter(activeTaskBean.id, tasksRepository, taskDetailView)
        taskDetailPresenter.start()

        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.id), getTaskCallBackArgumentCaptor.capture())

        getTaskCallBackArgumentCaptor.value.onDataNotAvailable()

        verify(taskDetailView).showMessage(MessageMap.NO_DATA)
    }

    @Test
    fun completeTask() {
        // Given an initialized presenter with an active task
        taskDetailPresenter = TaskDetailPresenter(activeTaskBean.id, tasksRepository, taskDetailView)

        // When the presenter is asked to complete the task
        taskDetailPresenter.completeTask()

        // Then a request is sent to the task repository and the UI is updated
        verify(tasksRepository).completeTask(activeTaskBean.id)
        verify(taskDetailView).showMessage(MessageMap.COMPLETE)
    }

    @Test
    fun activateTask() {
        // Given an initialized presenter with a completed task
        taskDetailPresenter = TaskDetailPresenter(completedTaskBean.id, tasksRepository, taskDetailView)

        // When the presenter is asked to activate the task
        taskDetailPresenter.activateTask()

        verify(tasksRepository).activateTask(completedTaskBean.id)
        verify(taskDetailView).showMessage(MessageMap.ACTIVE)
    }

    @Test
    fun deleteTask() {
        // When the deletion of a task is requested
        taskDetailPresenter = TaskDetailPresenter(activeTaskBean.id, tasksRepository, taskDetailView)
        taskDetailPresenter.deleteTask()

        // Then the repository and the view are notified
        verify(tasksRepository).deleteTask(activeTaskBean.id)
        verify(taskDetailView).deleteTask()
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}