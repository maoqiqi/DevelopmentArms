package com.codearms.maoqiqi.app.data.source.room;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.schedulers.ImmediateSchedulerProvider;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the {@link TasksDataSource}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 14:15
 */
@RunWith(AndroidJUnit4.class)
public class TasksRoomDataSourceTest {

    private static final TaskBean TASK_BEAN = new TaskBean("ID", "TITLE", "DESCRIPTION", true);

    private TasksRoomDataSource tasksRoomDataSource;

    @Before
    public void setUp() {
        tasksRoomDataSource = TasksRoomDataSource.getInstance(InstrumentationRegistry.getTargetContext(), new ImmediateSchedulerProvider());
        tasksRoomDataSource.deleteAllTasks();
    }

    @After
    public void tearDown() {
        TasksRoomDataSource.clearInstance();
    }

    @Test
    public void loadTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        TestSubscriber<List<TaskBean>> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.loadTasks().toFlowable().subscribe(testSubscriber);
        testSubscriber.assertValueCount(1);
    }

    @Test
    public void clearCompletedTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.clearCompletedTasks();

        TestSubscriber<TaskBean> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.getTask(TASK_BEAN.getId()).toFlowable().subscribe(testSubscriber);
        testSubscriber.assertValueCount(0);
    }

    @Test
    public void refreshTasks() {

    }

    @Test
    public void addTask() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        TestSubscriber<TaskBean> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.getTask(TASK_BEAN.getId()).toFlowable().subscribe(testSubscriber);
        assertTask(testSubscriber.values().get(0), TASK_BEAN);
    }

    @Test
    public void updateTask() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        final TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        tasksRoomDataSource.updateTask(newTaskBean);

        TestSubscriber<TaskBean> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.getTask(newTaskBean.getId()).toFlowable().subscribe(testSubscriber);
        assertTask(testSubscriber.values().get(0), newTaskBean);
    }

    @Test
    public void completeTask() {
        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        tasksRoomDataSource.addTask(newTaskBean);
        tasksRoomDataSource.completeTask(newTaskBean.getId());

        TestSubscriber<TaskBean> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.getTask(newTaskBean.getId()).toFlowable().subscribe(testSubscriber);
        assertTrue(testSubscriber.values().get(0).isCompleted());
    }

    @Test
    public void activateTask() {
        tasksRoomDataSource.addTask(TASK_BEAN);
        tasksRoomDataSource.activateTask(TASK_BEAN.getId());

        TestSubscriber<TaskBean> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.getTask(TASK_BEAN.getId()).toFlowable().subscribe(testSubscriber);
        assertTrue(testSubscriber.values().get(0).isActive());
    }

    @Test
    public void deleteTask() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.deleteTask(TASK_BEAN.getId());

        TestSubscriber<List<TaskBean>> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.loadTasks().subscribe();
        testSubscriber.assertValueCount(0);
    }

    @Test
    public void deleteAllTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.deleteAllTasks();

        TestSubscriber<List<TaskBean>> testSubscriber = new TestSubscriber<>();
        tasksRoomDataSource.loadTasks();
        testSubscriber.assertValueCount(0);
    }

    private void assertTask(TaskBean loaded, TaskBean taskBean) {
        assertThat(loaded, Matchers.notNullValue());
        assertThat(taskBean, Matchers.notNullValue());
        assertEquals(taskBean.getId(), loaded.getId());
        assertEquals(taskBean.getTitle(), loaded.getTitle());
        assertEquals(taskBean.getDescription(), loaded.getDescription());
        assertEquals(taskBean.isCompleted(), loaded.isCompleted());
    }
}