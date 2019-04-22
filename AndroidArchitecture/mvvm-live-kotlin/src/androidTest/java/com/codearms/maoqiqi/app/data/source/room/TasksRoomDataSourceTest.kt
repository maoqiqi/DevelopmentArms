package com.codearms.maoqiqi.app.data.source.room

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.utils.SingleExecutors
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
 * Date: 2019/3/13 14:15
 */
@RunWith(AndroidJUnit4::class)
class TasksRoomDataSourceTest {

    private lateinit var tasksRoomDataSource: TasksRoomDataSource

    @Before
    fun setUp() {
        tasksRoomDataSource = TasksRoomDataSource.getInstance(InstrumentationRegistry.getTargetContext(), SingleExecutors())
        tasksRoomDataSource.deleteAllTasks()
    }

    @After
    fun tearDown() {
        TasksRoomDataSource.clearInstance()
    }

    @Test
    fun loadTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN)
        tasksRoomDataSource.loadTasks(object : TasksDataSource.LoadTasksCallBack {
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
        tasksRoomDataSource.addTask(TASK_BEAN)

        tasksRoomDataSource.clearCompletedTasks()

        val callBack = mock(TasksDataSource.GetTaskCallBack::class.java)
        tasksRoomDataSource.getTask(TASK_BEAN.id, callBack)

        verify(callBack).onDataNotAvailable()
        verify(callBack, never()).onTaskLoaded(any(TaskBean::class.java))
    }

    @Test
    fun refreshTasks() {

    }

    @Test
    fun addTask() {
        tasksRoomDataSource.addTask(TASK_BEAN)
        tasksRoomDataSource.getTask(TASK_BEAN.id, object : TasksDataSource.GetTaskCallBack {
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
        tasksRoomDataSource.addTask(TASK_BEAN)

        val newTaskBean = TaskBean(TASK_BEAN.id, TASK_BEAN.title!!, TASK_BEAN.description!!, false)
        tasksRoomDataSource.updateTask(newTaskBean)

        tasksRoomDataSource.getTask(newTaskBean.id, object : TasksDataSource.GetTaskCallBack {
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
        tasksRoomDataSource.addTask(newTaskBean)
        tasksRoomDataSource.completeTask(newTaskBean.id)
        tasksRoomDataSource.getTask(newTaskBean.id, object : TasksDataSource.GetTaskCallBack {
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
        tasksRoomDataSource.addTask(TASK_BEAN)
        tasksRoomDataSource.activateTask(TASK_BEAN.id)
        tasksRoomDataSource.getTask(TASK_BEAN.id, object : TasksDataSource.GetTaskCallBack {
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
        tasksRoomDataSource.addTask(TASK_BEAN)

        tasksRoomDataSource.deleteTask(TASK_BEAN.id)

        val callBack = mock(TasksDataSource.LoadTasksCallBack::class.java)
        tasksRoomDataSource.loadTasks(callBack)

        verify(callBack).onDataNotAvailable()
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean::class.java))
    }

    @Test
    fun deleteAllTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN)

        tasksRoomDataSource.deleteAllTasks()

        val callBack = mock(TasksDataSource.LoadTasksCallBack::class.java)
        tasksRoomDataSource.loadTasks(callBack)

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