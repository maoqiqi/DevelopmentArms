package com.codearms.maoqiqi.app.data.source;

import com.codearms.maoqiqi.app.data.TaskBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

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

    private TasksRepository tasksRepository;
    private TestSubscriber<List<TaskBean>> tasksTestSubscriber;

    private TaskBean activeTaskBean;
    private TaskBean completedTaskBean;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        tasksRepository = TasksRepository.getInstance(tasksRemoteDataSource, tasksLocalDataSource);
        tasksTestSubscriber = new TestSubscriber<>();

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
        setTasksAvailable(tasksLocalDataSource, taskBeanList);
        setTasksNotAvailable(tasksRemoteDataSource);

        // When calling getTasks in the repository
        tasksRepository.loadTasks().subscribe(tasksTestSubscriber);
        // Then tasks were only requested once from remote and local sources
        verify(tasksLocalDataSource).loadTasks();
        verify(tasksRemoteDataSource).loadTasks();

        // Second call to API
        tasksRepository.loadTasks();
        // Then tasks were only requested once from Service API
        verify(tasksRemoteDataSource, times(1)).loadTasks();
        verify(tasksLocalDataSource, times(1)).loadTasks();
    }

    @Test
    public void getTask() {
        tasksRepository.getTask(activeTaskBean.getId());
        // if you use the parameter matcher, all parameters should use the parameter matcher.
        verify(tasksLocalDataSource).getTask(Mockito.eq(activeTaskBean.getId()));
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
        tasksRepository.loadTasks();

        // And the remote data source has data available
        verify(tasksRemoteDataSource).loadTasks();

        verify(tasksLocalDataSource, Mockito.never()).loadTasks();
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


    private void setTasksNotAvailable(TasksDataSource dataSource) {
        Mockito.when(dataSource.loadTasks()).thenReturn(Flowable.just(Collections.<TaskBean>emptyList()));
    }

    private void setTasksAvailable(TasksDataSource dataSource, List<TaskBean> tasks) {
        // don't allow the data sources to complete.
        Mockito.when(dataSource.loadTasks()).thenReturn(Flowable.just(tasks).concatWith(Flowable.<List<TaskBean>>never()));
    }

    private void setTaskNotAvailable(TasksDataSource dataSource, String taskId) {
        Mockito.when(dataSource.getTask(Mockito.eq(taskId))).thenReturn(Flowable.<TaskBean>empty());
    }

    private void setTaskAvailable(TasksDataSource dataSource, TaskBean taskBean) {
        Mockito.when(dataSource.getTask(Mockito.eq(taskBean.getId()))).thenReturn(Flowable.just(taskBean).concatWith(Flowable.<TaskBean>never()));
    }
}