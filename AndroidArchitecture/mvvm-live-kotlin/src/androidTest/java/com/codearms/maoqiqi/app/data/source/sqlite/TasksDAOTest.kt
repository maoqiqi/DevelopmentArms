package com.codearms.maoqiqi.app.data.source.sqlite

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.codearms.maoqiqi.app.data.TaskBean
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the [TasksDAO].
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 11:09
 */
@RunWith(AndroidJUnit4::class)
class TasksDAOTest {

    private lateinit var dao: TasksDAO

    @Before
    fun setUp() {
        dao = TasksDAO(InstrumentationRegistry.getTargetContext())
        dao.deleteTasks()
    }

    @Test
    fun loadTasks() {
        // When inserting a task
        dao.addTask(TASK_BEAN)

        // When getting the tasks
        val list = dao.loadTasks()

        // The list size = 1
        assertEquals(list.size.toLong(), 1)
        assertTask(list[0], TASK_BEAN)
    }

    @Test
    fun addTask() {
        dao.addTask(TASK_BEAN)

        // When getting the task by id from the database
        val loaded = dao.getTaskById(TASK_BEAN.id)

        // The loaded data contains the expected values
        assertTask(loaded, TASK_BEAN)
    }

    @Test
    fun updateTask() {
        dao.addTask(TASK_BEAN)

        // When a task with the same id is inserted
        val newTaskBean = TaskBean(TASK_BEAN.id, "NEW_TITLE", "NEW_DESCRIPTION", false)
        dao.updateTask(newTaskBean)

        val loaded = dao.getTaskById(newTaskBean.id)
        assertTask(loaded, newTaskBean)
    }

    @Test
    fun updateCompleted() {
        dao.addTask(TASK_BEAN)

        // When the task is updated
        dao.updateCompleted(TASK_BEAN.id, false)

        val loaded = dao.getTaskById(TASK_BEAN.id)
        val newTaskBean = TaskBean(TASK_BEAN.id, TASK_BEAN.title!!, TASK_BEAN.description!!, false)
        assertTask(loaded, newTaskBean)
    }

    @Test
    fun deleteTaskById() {
        dao.addTask(TASK_BEAN)

        // When deleting a task by id
        dao.deleteTaskById(TASK_BEAN.id)

        val list = dao.loadTasks()
        // The list is empty
        assertEquals(list.size.toLong(), 0)
    }

    @Test
    fun deleteCompletedTasks() {
        dao.addTask(TASK_BEAN)

        // When deleting completed tasks
        dao.deleteCompletedTasks()

        val list = dao.loadTasks()
        assertEquals(list.size.toLong(), 0)
    }

    @Test
    fun deleteTasks() {
        dao.addTask(TASK_BEAN)
        dao.deleteTasks()

        val list = dao.loadTasks()
        assertEquals(list.size.toLong(), 0)
    }

    private fun assertTask(loaded: TaskBean?, taskBean: TaskBean) {
        MatcherAssert.assertThat<TaskBean>(loaded as TaskBean, Matchers.notNullValue())
        assertEquals(taskBean.id, loaded.id)
        assertEquals(taskBean.title, loaded.title)
        assertEquals(taskBean.description, loaded.description)
        assertEquals(taskBean.isCompleted, loaded.isCompleted)
    }

    companion object {
        private val TASK_BEAN = TaskBean("ID", "TITLE", "DESCRIPTION", true)
    }
}