package com.codearms.maoqiqi.app.data.source.room;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Integration test for the {@link TasksDAO}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 11:39
 */
@RunWith(AndroidJUnit4.class)
public class TasksDAOTest {

    private static final TaskBean TASK_BEAN = new TaskBean("ID", "TITLE", "DESCRIPTION", true);

    private TaskDataBase taskDataBase;
    private TasksDAO dao;

    @Before
    public void setUp() {
        taskDataBase = TaskDataBase.getInstance(InstrumentationRegistry.getTargetContext());
        dao = taskDataBase.tasksDAO();
        dao.deleteTasks();
    }

    @After
    public void tearDown() {
        taskDataBase.close();
    }

    @Test
    public void loadTasks() {
        dao.addTask(TASK_BEAN);
        List<TaskBean> list = dao.loadTasks();
        assertEquals(list.size(), 1);
        assertTask(list.get(0), TASK_BEAN);
    }

    @Test
    public void addTask() {
        dao.addTask(TASK_BEAN);
        TaskBean loaded = dao.getTaskById(TASK_BEAN.getId());
        assertTask(loaded, TASK_BEAN);
    }

    @Test
    public void updateTask() {
        dao.addTask(TASK_BEAN);

        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), "NEW_TITLE", "NEW_DESCRIPTION", false);
        int update = dao.updateTask(newTaskBean);
        assertEquals(update, 1);

        TaskBean loaded = dao.getTaskById(newTaskBean.getId());
        assertTask(loaded, newTaskBean);
    }

    @Test
    public void updateCompleted() {
        dao.addTask(TASK_BEAN);

        dao.updateCompleted(TASK_BEAN.getId(), false);

        TaskBean loaded = dao.getTaskById(TASK_BEAN.getId());
        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        assertTask(loaded, newTaskBean);
    }

    @Test
    public void deleteTaskById() {
        dao.addTask(TASK_BEAN);

        int delete = dao.deleteTaskById(TASK_BEAN.getId());
        assertEquals(delete, 1);

        List<TaskBean> list = dao.loadTasks();
        assertEquals(list.size(), 0);
    }

    @Test
    public void deleteCompletedTasks() {
        dao.addTask(TASK_BEAN);

        int delete = dao.deleteCompletedTasks();
        assertEquals(delete, 1);

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