package com.codearms.maoqiqi.app.data.source.sqlite

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

/**
 * Integration test for the [TasksDataSource].
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 12:11
 */
@RunWith(AndroidJUnit4::class)
class TasksSqliteDataSourceTest {

    private lateinit var tasksSqliteDataSource: TasksSqliteDataSource

    @Before
    fun setUp() {
        tasksSqliteDataSource = TasksSqliteDataSource.getInstance(InstrumentationRegistry.getTargetContext())
        tasksSqliteDataSource.deleteAllTasks()
    }

    @After
    fun tearDown() {
        TasksSqliteDataSource.clearInstance()
    }

    @Test
    fun loadTasks() {
        // When saved into the persistent repository
        tasksSqliteDataSource.addTask(TASK_BEAN)

        // Then getting the tasks
        tasksSqliteDataSource.loadTasks(object : TasksDataSource.LoadTasksCallBack {
            override fun onTasksLoaded(taskBeanList: List<TaskBean>) {
                assertEquals(taskBeanList.size.toLong(), 1)
            }

            override fun onDataNotAvailable() {
                fail("Callback error")
            }
        })
    }

    @Test
    fun clearCompletedTasks() {
        // Given a new completed task in the persistent repository
        tasksSqliteDataSource.addTask(TASK_BEAN)

        // When completed tasks are cleared in the repository
        tasksSqliteDataSource.clearCompletedTasks()

        // Then the completed tasks cannot be retrieved and the active one can
        val callBack = mock(TasksDataSource.GetTaskCallBack::class.java)
        tasksSqliteDataSource.getTask(TASK_BEAN.id, callBack)

        verify(callBack).onDataNotAvailable()
        verify(callBack, never()).onTaskLoaded(any(TaskBean::class.java))
    }

    @Test
    fun refreshTasks() {

    }

    @Test
    fun addTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN)

        // Then getting the task by id from the database
        tasksSqliteDataSource.getTask(TASK_BEAN.id, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                assertTask(taskBean, TASK_BEAN)
            }

            override fun onDataNotAvailable() {
                fail("Callback error")
            }
        })
    }

    @Test
    fun updateTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN)

        val newTaskBean = TaskBean(TASK_BEAN.id, TASK_BEAN.title!!, TASK_BEAN.description!!, false)
        tasksSqliteDataSource.updateTask(newTaskBean)

        tasksSqliteDataSource.getTask(newTaskBean.id, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                assertTask(taskBean, newTaskBean)
            }

            override fun onDataNotAvailable() {
                fail("Callback error")
            }
        })
    }

    @Test
    fun completeTask() {
        val newTaskBean = TaskBean(TASK_BEAN.id, TASK_BEAN.title!!, TASK_BEAN.description!!, false)
        tasksSqliteDataSource.addTask(newTaskBean)

        // When completed in the persistent repository
        tasksSqliteDataSource.completeTask(newTaskBean.id)

        // Then getting the task by id from the database and is complete
        tasksSqliteDataSource.getTask(newTaskBean.id, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                assertTrue(taskBean.isCompleted)
            }

            override fun onDataNotAvailable() {
                fail("Callback error")
            }
        })
    }

    @Test
    fun activateTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN)

        // When activated in the persistent repository
        tasksSqliteDataSource.activateTask(TASK_BEAN.id)

        // Then getting the task by id from the database and is active
        tasksSqliteDataSource.getTask(TASK_BEAN.id, object : TasksDataSource.GetTaskCallBack {
            override fun onTaskLoaded(taskBean: TaskBean) {
                assertTrue(taskBean.isActive)
            }

            override fun onDataNotAvailable() {
                fail("Callback error")
            }
        })
    }

    @Test
    fun deleteTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN)

        tasksSqliteDataSource.deleteTask(TASK_BEAN.id)

        val callBack = mock(TasksDataSource.LoadTasksCallBack::class.java)
        tasksSqliteDataSource.loadTasks(callBack)

        verify(callBack).onDataNotAvailable()
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean::class.java))
    }

    @Test
    fun deleteAllTasks() {
        tasksSqliteDataSource.addTask(TASK_BEAN)

        tasksSqliteDataSource.deleteAllTasks()

        val callBack = mock(TasksDataSource.LoadTasksCallBack::class.java)
        tasksSqliteDataSource.loadTasks(callBack)

        verify(callBack).onDataNotAvailable()
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean::class.java))
    }

    private fun assertTask(loaded: TaskBean, taskBean: TaskBean) {
        assertThat(loaded, Matchers.notNullValue())
        assertThat(taskBean, Matchers.notNullValue())
        assertEquals(taskBean.id, loaded.id)
        assertEquals(taskBean.title, loaded.title)
        assertEquals(taskBean.description, loaded.description)
        assertEquals(taskBean.isCompleted, loaded.isCompleted)
    }

    companion object {
        private val TASK_BEAN = TaskBean("ID", "TITLE", "DESCRIPTION", true)
    }
}