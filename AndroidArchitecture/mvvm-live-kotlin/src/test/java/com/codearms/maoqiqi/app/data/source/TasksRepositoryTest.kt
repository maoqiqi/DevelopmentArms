package com.codearms.maoqiqi.app.data.source

import com.codearms.maoqiqi.app.data.TaskBean
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.util.*

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 15:17
 */
class TasksRepositoryTest {

    @Mock
    private lateinit var taskRemoteDataSource: TaskDataSource
    @Mock
    private lateinit var taskLocalDataSource: TaskDataSource

    @Mock
    private lateinit var loadTaskCallBack: TaskDataSource.LoadTasksCallBack
    @Mock
    private lateinit var getTaskCallBack: TaskDataSource.GetTaskCallBack

    /**
     * Capture argument values and use them to perform further actions or assertions on them.
     */
    @Captor
    private lateinit var loadTaskCallBackArgumentCaptor: ArgumentCaptor<TaskDataSource.LoadTasksCallBack>

    private lateinit var tasksRepository: TaskRepository

    private lateinit var activeTaskBean: TaskBean
    private lateinit var completedTaskBean: TaskBean

    private lateinit var taskBeanList: MutableList<TaskBean>

    @Before
    fun setUp() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        tasksRepository = TaskRepository.getInstance(taskRemoteDataSource, taskLocalDataSource)

        activeTaskBean = TaskBean(TITLE, DESCRIPTION, false)
        completedTaskBean = TaskBean(TITLE, DESCRIPTION, true)

        taskBeanList = ArrayList()
        taskBeanList.add(activeTaskBean)
        taskBeanList.add(completedTaskBean)
    }

    @After
    fun tearDown() {
        TaskRepository.destroyInstance()
    }

    @Test
    fun loadTasks() {
        // When calling getTasks in the repository
        tasksRepository.loadTasks(loadTaskCallBack)
        // Use the Mockito Captor to capture the callback
        verify(taskLocalDataSource).loadTasks(loadTaskCallBackArgumentCaptor.capture())

        // And the local data source has no data available
        loadTaskCallBackArgumentCaptor.value.onDataNotAvailable()
        // Verify the remote data source is queried
        verify(taskRemoteDataSource).loadTasks(loadTaskCallBackArgumentCaptor.capture())

        // And the remote data source has data available
        loadTaskCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)
        // Verify the tasks from the local data source are returned
        verify(loadTaskCallBack).onTasksLoaded(taskBeanList)

        // Second call to API
        tasksRepository.loadTasks(loadTaskCallBack)
        // Then tasks were only requested once from Service API
        verify(taskRemoteDataSource, times(1)).loadTasks(Mockito.any(TaskDataSource.LoadTasksCallBack::class.java))
        verify(taskLocalDataSource, times(1)).loadTasks(Mockito.any(TaskDataSource.LoadTasksCallBack::class.java))
    }

    @Test
    fun getTask() {
        tasksRepository.getTask(activeTaskBean.id, getTaskCallBack)
        // if you use the parameter matcher, all parameters should use the parameter matcher.
        verify(taskLocalDataSource).getTask(Mockito.eq(activeTaskBean.id), Mockito.any(TaskDataSource.GetTaskCallBack::class.java))
    }

    @Test
    fun clearCompletedTasks() {
        for (i in taskBeanList.indices) {
            tasksRepository.addTask(taskBeanList[i])
        }

        // When a completed tasks are cleared to the tasks repository
        tasksRepository.clearCompletedTasks()

        // Then the service API and persistent repository are called and the cache is updated
        verify(taskRemoteDataSource).clearCompletedTasks()
        verify(taskLocalDataSource).clearCompletedTasks()

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
    }

    @Test
    fun refreshTasks() {
        // When calling getTasks in the repository with dirty cache
        tasksRepository.refreshTasks()
        tasksRepository.loadTasks(loadTaskCallBack)

        // And the remote data source has data available
        verify(taskRemoteDataSource).loadTasks(loadTaskCallBackArgumentCaptor.capture())
        loadTaskCallBackArgumentCaptor.value.onTasksLoaded(taskBeanList)

        verify(taskLocalDataSource, Mockito.never()).loadTasks(loadTaskCallBack)
        verify(loadTaskCallBack).onTasksLoaded(taskBeanList)
    }

    @Test
    fun addTask() {
        // When a task is saved to the tasks repository
        tasksRepository.addTask(activeTaskBean)

        // Then the service API and persistent repository are called and the cache is updated
        verify(taskRemoteDataSource).addTask(activeTaskBean)
        verify(taskLocalDataSource).addTask(activeTaskBean)
        assertEquals(tasksRepository.cachedTasksMap.size, 1)
    }

    @Test
    fun updateTask() {
        tasksRepository.addTask(activeTaskBean)

        val taskBean = TaskBean(activeTaskBean.id, NEW_TITLE, NEW_DESCRIPTION, false)
        tasksRepository.updateTask(taskBean)

        verify(taskRemoteDataSource).updateTask(taskBean)
        verify(taskLocalDataSource).updateTask(taskBean)

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
        assertEquals(tasksRepository.getTaskById(activeTaskBean.id)?.title, NEW_TITLE)
    }

    @Test
    fun completeTask() {
        tasksRepository.addTask(activeTaskBean)

        // When a active task is completed to the tasks repository
        tasksRepository.completeTask(activeTaskBean)

        // Then the service API and persistent repository are called and the cache is updated
        verify(taskRemoteDataSource).completeTask(activeTaskBean)
        verify(taskLocalDataSource).completeTask(activeTaskBean)

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
        assertEquals(tasksRepository.getTaskById(activeTaskBean.id)?.isCompleted, true)
    }

    @Test
    fun completeTaskId() {
        tasksRepository.addTask(activeTaskBean)

        // When a active task is completed to the tasks repository
        tasksRepository.completeTask(activeTaskBean.id)

        // Then the service API and persistent repository are called and the cache is updated
        verify(taskRemoteDataSource).completeTask(activeTaskBean)
        verify(taskLocalDataSource).completeTask(activeTaskBean)

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
        assertEquals(tasksRepository.getTaskById(activeTaskBean.id)?.isCompleted, true)
    }

    @Test
    fun activateTask() {
        tasksRepository.addTask(completedTaskBean)

        // When a completed task is activated to the tasks repository
        tasksRepository.activateTask(completedTaskBean)

        verify(taskRemoteDataSource).activateTask(completedTaskBean)
        verify(taskLocalDataSource).activateTask(completedTaskBean)

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
        assertEquals(tasksRepository.getTaskById(completedTaskBean.id)?.isActive, true)
    }

    @Test
    fun activateTaskId() {
        tasksRepository.addTask(completedTaskBean)

        // When a completed task is activated to the tasks repository
        tasksRepository.activateTask(completedTaskBean.id)

        verify(taskRemoteDataSource).activateTask(completedTaskBean)
        verify(taskLocalDataSource).activateTask(completedTaskBean)

        assertEquals(tasksRepository.cachedTasksMap.size, 1)
        assertEquals(tasksRepository.getTaskById(completedTaskBean.id)?.isActive, true)
    }

    @Test
    fun deleteTask() {
        tasksRepository.addTask(activeTaskBean)
        assertTrue(tasksRepository.cachedTasksMap.containsKey(activeTaskBean.id))

        // When deleted
        tasksRepository.deleteTask(activeTaskBean.id)

        // Verify the data sources were called
        verify(taskRemoteDataSource).deleteTask(activeTaskBean.id)
        verify(taskLocalDataSource).deleteTask(activeTaskBean.id)

        // Verify it's removed from repository
        assertFalse(tasksRepository.cachedTasksMap.containsKey(activeTaskBean.id))
    }

    @Test
    fun deleteAllTasks() {
        for (i in taskBeanList.indices) {
            tasksRepository.addTask(taskBeanList[i])
        }

        // When all tasks are deleted to the tasks repository
        tasksRepository.deleteAllTasks()

        // Verify the data sources were called
        verify(taskRemoteDataSource).deleteAllTasks()
        verify(taskLocalDataSource).deleteAllTasks()

        assertEquals(tasksRepository.cachedTasksMap.size, 0)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"

        private const val NEW_TITLE = "NEW_TITLE"
        private const val NEW_DESCRIPTION = "NEW_DESCRIPTION"
    }
}