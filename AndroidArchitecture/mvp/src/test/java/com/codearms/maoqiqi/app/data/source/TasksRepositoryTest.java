package com.codearms.maoqiqi.app.data.source;

import com.codearms.maoqiqi.app.data.TaskBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 15:17
 */
public class TasksRepositoryTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    private final static String NEW_TITLE = "NEW_TITLE";
    private final static String NEW_DESCRIPTION = "NEW_DESCRIPTION";

    @Mock
    private TasksDataSource tasksRemoteDataSource;
    @Mock
    private TasksDataSource tasksLocalDataSource;

    @Mock
    private TasksDataSource.LoadTasksCallBack loadTasksCallBack;
    @Mock
    private TasksDataSource.GetTaskCallBack getTaskCallBack;

    /**
     * Capture argument values and use them to perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<TasksDataSource.LoadTasksCallBack> loadTasksCallBackArgumentCaptor;

    private TasksRepository tasksRepository;

    private TaskBean activeTaskBean;
    private TaskBean completedTaskBean;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        tasksRepository = TasksRepository.getInstance(tasksRemoteDataSource, tasksLocalDataSource);

        activeTaskBean = new TaskBean(TITLE, DESCRIPTION, false);
        completedTaskBean = new TaskBean(TITLE, DESCRIPTION, true);

        taskBeanList = new ArrayList<>();
        taskBeanList.add(activeTaskBean);
        taskBeanList.add(completedTaskBean);
    }

    @After
    public void tearDown() {
        TasksRepository.destroyInstance();
    }

    @Test
    public void loadTasks() {
        // When calling getTasks in the repository
        tasksRepository.loadTasks(loadTasksCallBack);
        // Use the Mockito Captor to capture the callback
        verify(tasksLocalDataSource).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        // And the local data source has no data available
        loadTasksCallBackArgumentCaptor.getValue().onDataNotAvailable();
        // Verify the remote data source is queried
        verify(tasksRemoteDataSource).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        // And the remote data source has data available
        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);
        // Verify the tasks from the local data source are returned
        verify(loadTasksCallBack).onTasksLoaded(taskBeanList);

        // Second call to API
        tasksRepository.loadTasks(loadTasksCallBack);
        // Then tasks were only requested once from Service API
        verify(tasksRemoteDataSource, times(1)).loadTasks(Mockito.any(TasksDataSource.LoadTasksCallBack.class));
        verify(tasksLocalDataSource, times(1)).loadTasks(Mockito.any(TasksDataSource.LoadTasksCallBack.class));
    }

    @Test
    public void getTask() {
        tasksRepository.getTask(activeTaskBean.getId(), getTaskCallBack);
        // if you use the parameter matcher, all parameters should use the parameter matcher.
        verify(tasksLocalDataSource).getTask(Mockito.eq(activeTaskBean.getId()), Mockito.any(TasksDataSource.GetTaskCallBack.class));
    }

    @Test
    public void clearCompletedTasks() {
        for (int i = 0; i < taskBeanList.size(); i++) {
            tasksRepository.addTask(taskBeanList.get(i));
        }

        // When a completed tasks are cleared to the tasks repository
        tasksRepository.clearCompletedTasks();

        // Then the service API and persistent repository are called and the cache is updated
        verify(tasksRemoteDataSource).clearCompletedTasks();
        verify(tasksLocalDataSource).clearCompletedTasks();

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
    }

    @Test
    public void refreshTasks() {
        // When calling getTasks in the repository with dirty cache
        tasksRepository.refreshTasks();
        tasksRepository.loadTasks(loadTasksCallBack);

        // And the remote data source has data available
        verify(tasksRemoteDataSource).loadTasks(loadTasksCallBackArgumentCaptor.capture());
        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        verify(tasksLocalDataSource, Mockito.never()).loadTasks(loadTasksCallBack);
        verify(loadTasksCallBack).onTasksLoaded(taskBeanList);
    }

    @Test
    public void addTask() {
        // When a task is saved to the tasks repository
        tasksRepository.addTask(activeTaskBean);

        // Then the service API and persistent repository are called and the cache is updated
        verify(tasksRemoteDataSource).addTask(activeTaskBean);
        verify(tasksLocalDataSource).addTask(activeTaskBean);
        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
    }

    @Test
    public void updateTask() {
        tasksRepository.addTask(activeTaskBean);

        TaskBean taskBean = new TaskBean(activeTaskBean.getId(), NEW_TITLE, NEW_DESCRIPTION, false);
        tasksRepository.updateTask(taskBean);

        verify(tasksRemoteDataSource).updateTask(taskBean);
        verify(tasksLocalDataSource).updateTask(taskBean);

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
        assertEquals(tasksRepository.getTaskById(activeTaskBean.getId()).getTitle(), NEW_TITLE);
    }

    @Test
    public void completeTask() {
        tasksRepository.addTask(activeTaskBean);

        // When a active task is completed to the tasks repository
        tasksRepository.completeTask(activeTaskBean);

        // Then the service API and persistent repository are called and the cache is updated
        verify(tasksRemoteDataSource).completeTask(activeTaskBean);
        verify(tasksLocalDataSource).completeTask(activeTaskBean);

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
        assertTrue(tasksRepository.getTaskById(activeTaskBean.getId()).isCompleted());
    }

    @Test
    public void completeTaskId() {
        tasksRepository.addTask(activeTaskBean);

        // When a active task is completed to the tasks repository
        tasksRepository.completeTask(activeTaskBean.getId());

        // Then the service API and persistent repository are called and the cache is updated
        verify(tasksRemoteDataSource).completeTask(activeTaskBean);
        verify(tasksLocalDataSource).completeTask(activeTaskBean);

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
        assertTrue(tasksRepository.getTaskById(activeTaskBean.getId()).isCompleted());
    }

    @Test
    public void activateTask() {
        tasksRepository.addTask(completedTaskBean);

        // When a completed task is activated to the tasks repository
        tasksRepository.activateTask(completedTaskBean);

        verify(tasksRemoteDataSource).activateTask(completedTaskBean);
        verify(tasksLocalDataSource).activateTask(completedTaskBean);

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
        assertTrue(tasksRepository.getTaskById(completedTaskBean.getId()).isActive());
    }

    @Test
    public void activateTaskId() {
        tasksRepository.addTask(completedTaskBean);

        // When a completed task is activated to the tasks repository
        tasksRepository.activateTask(completedTaskBean.getId());

        verify(tasksRemoteDataSource).activateTask(completedTaskBean);
        verify(tasksLocalDataSource).activateTask(completedTaskBean);

        assertEquals(tasksRepository.cachedTasksMap.size(), 1);
        assertTrue(tasksRepository.getTaskById(completedTaskBean.getId()).isActive());
    }

    @Test
    public void deleteTask() {
        tasksRepository.addTask(activeTaskBean);
        assertTrue(tasksRepository.cachedTasksMap.containsKey(activeTaskBean.getId()));

        // When deleted
        tasksRepository.deleteTask(activeTaskBean.getId());

        // Verify the data sources were called
        verify(tasksRemoteDataSource).deleteTask(activeTaskBean.getId());
        verify(tasksLocalDataSource).deleteTask(activeTaskBean.getId());

        // Verify it's removed from repository
        assertFalse(tasksRepository.cachedTasksMap.containsKey(activeTaskBean.getId()));
    }

    @Test
    public void deleteAllTasks() {
        for (int i = 0; i < taskBeanList.size(); i++) {
            tasksRepository.addTask(taskBeanList.get(i));
        }

        // When all tasks are deleted to the tasks repository
        tasksRepository.deleteAllTasks();

        // Verify the data sources were called
        verify(tasksRemoteDataSource).deleteAllTasks();
        verify(tasksLocalDataSource).deleteAllTasks();

        assertEquals(tasksRepository.cachedTasksMap.size(), 0);
    }
}