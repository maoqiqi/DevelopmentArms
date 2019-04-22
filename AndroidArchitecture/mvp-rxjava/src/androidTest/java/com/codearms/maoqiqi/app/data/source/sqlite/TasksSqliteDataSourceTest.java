package com.codearms.maoqiqi.app.data.source.sqlite;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration test for the {@link TasksDataSource}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 12:11
 */
@RunWith(AndroidJUnit4.class)
public class TasksSqliteDataSourceTest {

    private static final TaskBean TASK_BEAN = new TaskBean("ID", "TITLE", "DESCRIPTION", true);

    private TasksSqliteDataSource tasksSqliteDataSource;

    @Before
    public void setUp() {
        tasksSqliteDataSource = TasksSqliteDataSource.getInstance(InstrumentationRegistry.getTargetContext());
        tasksSqliteDataSource.deleteAllTasks();
    }

    @After
    public void tearDown() {
        TasksSqliteDataSource.clearInstance();
    }

    @Test
    public void loadTasks() {
        // When saved into the persistent repository
        tasksSqliteDataSource.addTask(TASK_BEAN);

        // Then getting the tasks
        tasksSqliteDataSource.loadTasks(new TasksDataSource.LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<TaskBean> taskBeanList) {
                assertEquals(taskBeanList.size(), 1);
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void clearCompletedTasks() {
        // Given a new completed task in the persistent repository
        tasksSqliteDataSource.addTask(TASK_BEAN);

        // When completed tasks are cleared in the repository
        tasksSqliteDataSource.clearCompletedTasks();

        // Then the completed tasks cannot be retrieved and the active one can
        TasksDataSource.GetTaskCallBack callBack = mock(TasksDataSource.GetTaskCallBack.class);
        tasksSqliteDataSource.getTask(TASK_BEAN.getId(), callBack);

        verify(callBack).onDataNotAvailable();
        verify(callBack, never()).onTaskLoaded(any(TaskBean.class));
    }

    @Test
    public void refreshTasks() {

    }

    @Test
    public void addTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN);

        // Then getting the task by id from the database
        tasksSqliteDataSource.getTask(TASK_BEAN.getId(), new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                assertTask(taskBean, TASK_BEAN);
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void updateTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN);

        final TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        tasksSqliteDataSource.updateTask(newTaskBean);

        tasksSqliteDataSource.getTask(newTaskBean.getId(), new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                assertTask(taskBean, newTaskBean);
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void completeTask() {
        TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        tasksSqliteDataSource.addTask(newTaskBean);

        // When completed in the persistent repository
        tasksSqliteDataSource.completeTask(newTaskBean.getId());

        // Then getting the task by id from the database and is complete
        tasksSqliteDataSource.getTask(newTaskBean.getId(), new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                assertTrue(taskBean.isCompleted());
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void activateTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN);

        // When activated in the persistent repository
        tasksSqliteDataSource.activateTask(TASK_BEAN.getId());

        // Then getting the task by id from the database and is active
        tasksSqliteDataSource.getTask(TASK_BEAN.getId(), new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                assertTrue(taskBean.isActive());
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void deleteTask() {
        tasksSqliteDataSource.addTask(TASK_BEAN);

        tasksSqliteDataSource.deleteTask(TASK_BEAN.getId());

        TasksDataSource.LoadTasksCallBack callBack = mock(TasksDataSource.LoadTasksCallBack.class);
        tasksSqliteDataSource.loadTasks(callBack);

        verify(callBack).onDataNotAvailable();
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean.class));
    }

    @Test
    public void deleteAllTasks() {
        tasksSqliteDataSource.addTask(TASK_BEAN);

        tasksSqliteDataSource.deleteAllTasks();

        TasksDataSource.LoadTasksCallBack callBack = mock(TasksDataSource.LoadTasksCallBack.class);
        tasksSqliteDataSource.loadTasks(callBack);

        verify(callBack).onDataNotAvailable();
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean.class));
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