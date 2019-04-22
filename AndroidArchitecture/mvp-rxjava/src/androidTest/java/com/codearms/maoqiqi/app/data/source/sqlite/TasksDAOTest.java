package com.codearms.maoqiqi.app.data.source.sqlite;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Integration test for the {@link TasksDAO}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 11:09
 */
@RunWith(AndroidJUnit4.class)
public class TasksDAOTest {

    private static final TaskBean TASK_BEAN = new TaskBean("ID", "TITLE", "DESCRIPTION", true);

    private TasksDAO dao;

    @Before
    public void setUp() {
        dao = new TasksDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteTasks();
    }

    @Test
    public void loadTasks() {
        // When inserting a task
        dao.addTask(TASK_BEAN);

        // When getting the tasks
        List<TaskBean> list = dao.loadTasks();

        // The list size = 1
        assertEquals(list.size(), 1);
        assertTask(list.get(0), TASK_BEAN);
    }

    @Test
    public void addTask() {
        dao.addTask(TASK_BEAN);

        // When getting the task by id from the database
        TaskBean loaded = dao.getTaskById(TASK_BEAN.getId());

        // The loaded data contains the expected values
        assertTask(loaded, TASK_BEAN);
    }

    @Test
    public void updateTask() {
        dao.addTask(TASK_BEAN);

        // When a task with the same id is inserted
        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), "NEW_TITLE", "NEW_DESCRIPTION", false);
        dao.updateTask(newTaskBean);

        TaskBean loaded = dao.getTaskById(newTaskBean.getId());
        assertTask(loaded, newTaskBean);
    }

    @Test
    public void updateCompleted() {
        dao.addTask(TASK_BEAN);

        // When the task is updated
        dao.updateCompleted(TASK_BEAN.getId(), false);

        TaskBean loaded = dao.getTaskById(TASK_BEAN.getId());
        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        assertTask(loaded, newTaskBean);
    }

    @Test
    public void deleteTaskById() {
        dao.addTask(TASK_BEAN);

        // When deleting a task by id
        dao.deleteTaskById(TASK_BEAN.getId());

        List<TaskBean> list = dao.loadTasks();
        // The list is empty
        assertEquals(list.size(), 0);
    }

    @Test
    public void deleteCompletedTasks() {
        dao.addTask(TASK_BEAN);

        // When deleting completed tasks
        dao.deleteCompletedTasks();

        List<TaskBean> list = dao.loadTasks();
        assertEquals(list.size(), 0);
    }

    @Test
    public void deleteTasks() {
        dao.addTask(TASK_BEAN);
        dao.deleteTasks();

        List<TaskBean> list = dao.loadTasks();
        assertEquals(list.size(), 0);
    }

    private void assertTask(TaskBean loaded, TaskBean taskBean) {
        assertThat(loaded, Matchers.notNullValue());
        assertEquals(taskBean.getId(), loaded.getId());
        assertEquals(taskBean.getTitle(), loaded.getTitle());
        assertEquals(taskBean.getDescription(), loaded.getDescription());
        assertEquals(taskBean.isCompleted(), loaded.isCompleted());
    }
}