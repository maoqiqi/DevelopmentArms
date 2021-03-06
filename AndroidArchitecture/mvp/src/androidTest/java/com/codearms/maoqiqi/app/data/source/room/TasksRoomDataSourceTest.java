package com.codearms.maoqiqi.app.data.source.room;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.SingleExecutors;

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
 * Date: 2019/3/13 14:15
 */
@RunWith(AndroidJUnit4.class)
public class TasksRoomDataSourceTest {

    private static final TaskBean TASK_BEAN = new TaskBean("ID", "TITLE", "DESCRIPTION", true);

    private TasksRoomDataSource tasksRoomDataSource;

    @Before
    public void setUp() {
        tasksRoomDataSource = TasksRoomDataSource.getInstance(InstrumentationRegistry.getTargetContext(), new SingleExecutors());
        tasksRoomDataSource.deleteAllTasks();
    }

    @After
    public void tearDown() {
        TasksRoomDataSource.clearInstance();
    }

    @Test
    public void loadTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN);
        tasksRoomDataSource.loadTasks(new TasksDataSource.LoadTasksCallBack() {
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
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.clearCompletedTasks();

        TasksDataSource.GetTaskCallBack callBack = mock(TasksDataSource.GetTaskCallBack.class);
        tasksRoomDataSource.getTask(TASK_BEAN.getId(), callBack);

        verify(callBack).onDataNotAvailable();
        verify(callBack, never()).onTaskLoaded(any(TaskBean.class));
    }

    @Test
    public void refreshTasks() {

    }

    @Test
    public void addTask() {
        tasksRoomDataSource.addTask(TASK_BEAN);
        tasksRoomDataSource.getTask(TASK_BEAN.getId(), new TasksDataSource.GetTaskCallBack() {
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
        tasksRoomDataSource.addTask(TASK_BEAN);

        final TaskBean newTaskBean = new TaskBean(TASK_BEAN.getId(), TASK_BEAN.getTitle(), TASK_BEAN.getDescription(), false);
        tasksRoomDataSource.updateTask(newTaskBean);

        tasksRoomDataSource.getTask(newTaskBean.getId(), new TasksDataSource.GetTaskCallBack() {
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
        tasksRoomDataSource.addTask(newTaskBean);
        tasksRoomDataSource.completeTask(newTaskBean.getId());
        tasksRoomDataSource.getTask(newTaskBean.getId(), new TasksDataSource.GetTaskCallBack() {
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
        tasksRoomDataSource.addTask(TASK_BEAN);
        tasksRoomDataSource.activateTask(TASK_BEAN.getId());
        tasksRoomDataSource.getTask(TASK_BEAN.getId(), new TasksDataSource.GetTaskCallBack() {
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
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.deleteTask(TASK_BEAN.getId());

        TasksDataSource.LoadTasksCallBack callBack = mock(TasksDataSource.LoadTasksCallBack.class);
        tasksRoomDataSource.loadTasks(callBack);

        verify(callBack).onDataNotAvailable();
        verify(callBack, never()).onTasksLoaded(anyListOf(TaskBean.class));
    }

    @Test
    public void deleteAllTasks() {
        tasksRoomDataSource.addTask(TASK_BEAN);

        tasksRoomDataSource.deleteAllTasks();

        TasksDataSource.LoadTasksCallBack callBack = mock(TasksDataSource.LoadTasksCallBack.class);
        tasksRoomDataSource.loadTasks(callBack);

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