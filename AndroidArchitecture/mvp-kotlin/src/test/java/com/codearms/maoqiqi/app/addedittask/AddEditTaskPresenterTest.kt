package com.codearms.maoqiqi.app.addedittask

import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.utils.MessageMap
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Unit tests for the implementation of [AddEditTaskPresenter].
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
class AddEditTaskPresenterTest {

    @Mock
    private lateinit var tasksRepository: TasksRepository
    @Mock
    private lateinit var addEditTaskView: AddEditTaskContract.View

    @Captor
    private lateinit var getTaskCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.GetTaskCallBack>

    private lateinit var addEditTaskPresenter: AddEditTaskPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(addEditTaskView.isActive).thenReturn(true)
    }

    @Test
    fun setPresenter() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true)
        verify(addEditTaskView).presenter = addEditTaskPresenter
    }

    @Test
    fun addTask() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true)

        // When the presenter is asked to save a task
        addEditTaskPresenter.addTask(TITLE, DESCRIPTION)

        // Then a task is saved in the repository and the view updated
        verify(tasksRepository).addTask(Mockito.any(TaskBean::class.java))
        verify(addEditTaskView).showTasks()
    }

    @Test
    fun addTaskError() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true)

        // When the presenter is asked to save an empty task
        addEditTaskPresenter.addTask("", "")

        // Then an empty not error is shown in the UI
        verify(addEditTaskView).showMessage(MessageMap.ENTER)
    }

    @Test
    fun addTaskExisting() {
        addEditTaskPresenter = AddEditTaskPresenter(ID, tasksRepository, addEditTaskView, true)

        // When the presenter is asked to save an existing task
        addEditTaskPresenter.addTask(TITLE, DESCRIPTION)

        verify(tasksRepository).updateTask(Mockito.any(TaskBean::class.java))
        verify(addEditTaskView).showTasks()
    }

    @Test
    fun getTask() {
        val taskBean = TaskBean(TITLE, DESCRIPTION, false)

        addEditTaskPresenter = AddEditTaskPresenter(taskBean.id, tasksRepository, addEditTaskView, true)

        // When the presenter is asked to populate an existing task
        addEditTaskPresenter.start()

        verify(tasksRepository).getTask(Mockito.eq(taskBean.id), getTaskCallBackArgumentCaptor.capture())
        assertTrue(addEditTaskPresenter.isDataMissing)

        getTaskCallBackArgumentCaptor.value.onTaskLoaded(taskBean)

        verify(addEditTaskView).setData(taskBean)
        assertFalse(addEditTaskPresenter.isDataMissing)
    }

    companion object {
        private const val ID = "ID"
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}