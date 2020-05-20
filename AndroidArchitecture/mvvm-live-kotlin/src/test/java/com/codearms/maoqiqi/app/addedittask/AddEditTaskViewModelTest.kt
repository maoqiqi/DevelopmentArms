package com.codearms.maoqiqi.app.addedittask


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.data.source.TasksRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Unit tests for the implementation of [AddEditTaskViewModel].
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
class AddEditTaskViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tasksRepository: TasksRepository

    @Captor
    private lateinit var getTaskCallBackArgumentCaptor: ArgumentCaptor<TasksDataSource.GetTaskCallBack>

    private lateinit var addEditTaskViewModel: AddEditTaskViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        addEditTaskViewModel = AddEditTaskViewModel(tasksRepository)
    }

    @Test
    fun addTask() {
        // When the ViewModel is asked to save a task
        addEditTaskViewModel.observableTitle.value = TITLE
        addEditTaskViewModel.observableDescription.value = DESCRIPTION
        addEditTaskViewModel.addTask()

        // Then a task is saved in the repository and the view updated
        verify(tasksRepository).addTask(Mockito.any(TaskBean::class.java))
    }

    @Test
    fun getTask() {
        val taskBean = TaskBean(TITLE, DESCRIPTION, false)

        addEditTaskViewModel.setTaskId(taskBean.id)

        // When the ViewModel is asked to getTask an existing task
        addEditTaskViewModel.start()

        verify(tasksRepository).getTask(Matchers.eq(taskBean.id), getTaskCallBackArgumentCaptor.capture())
        assertTrue(addEditTaskViewModel.isDataMissing)

        getTaskCallBackArgumentCaptor.value.onTaskLoaded(taskBean)

        assertEquals(addEditTaskViewModel.observableTitle.value, taskBean.title)
        assertEquals(addEditTaskViewModel.observableDescription.value, taskBean.description)

        assertFalse(addEditTaskViewModel.isDataMissing)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}