package com.codearms.maoqiqi.app.tasks;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

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
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TasksViewModel}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class TasksViewModelTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private TasksFragment tasksFragment;

    @Captor
    private ArgumentCaptor<TasksDataSource.LoadTasksCallBack> loadTasksCallBackArgumentCaptor;

    private TasksViewModel tasksViewModel;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        tasksViewModel = new TasksViewModel(tasksRepository);
        tasksViewModel.setTaskItemListener(tasksFragment);
        tasksViewModel.setCallBack(tasksFragment);

        taskBeanList = new ArrayList<>();
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, true));
    }

    @Test
    public void loadTasks() {
        // When loading of Tasks is requested
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS);
        tasksViewModel.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        // Then progress indicator is shown
        assertTrue(tasksViewModel.observableLoading.get());

        // When task is finally loaded
        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        // Then progress indicator is hidden
        assertFalse(tasksViewModel.observableLoading.get());

        // And data loaded
        assertEquals(tasksViewModel.observableList.size(), 3);
    }

    @Test
    public void loadActiveTasks() {
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS);
        tasksViewModel.loadTasks(true);

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());
        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        assertFalse(tasksViewModel.observableLoading.get());
        assertEquals(tasksViewModel.observableList.size(), 2);
    }

    @Test
    public void loadCompletedTasks() {
        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS);
        tasksViewModel.loadTasks(true);

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());
        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        assertFalse(tasksViewModel.observableLoading.get());
        assertEquals(tasksViewModel.observableList.size(), 1);
    }

    @Test
    public void showNoTasks() {
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS);
        tasksViewModel.loadTasks(true);

        // And the tasks aren't available in the repository
        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());
        loadTasksCallBackArgumentCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        assertTrue(tasksViewModel.observableNoTasks.get());
    }

    @Test
    public void clearCompletedTasks() {
        // When completed tasks are cleared
        tasksViewModel.clearCompletedTasks();

        // Then repository is called and the view is notified
        verify(tasksRepository).clearCompletedTasks();
        verify(tasksRepository).loadTasks(Mockito.any(TasksDataSource.LoadTasksCallBack.class));
    }

    @Test
    public void completeTask() {
        // Given a stubbed activated task
        TaskBean completedTaskBean = new TaskBean(TITLE, DESCRIPTION, false);

        // When task is marked as complete
        tasksViewModel.completeTask(completedTaskBean);

        // Then repository is called and task marked complete UI is shown
        verify(tasksRepository).completeTask(completedTaskBean);
        verify(tasksFragment).showMessage(MessageMap.COMPLETE);
    }

    @Test
    public void activateTask() {
        // Given a stubbed completed task
        TaskBean activeTaskBean = new TaskBean(TITLE, DESCRIPTION, true);

        // When task is marked as activated
        tasksViewModel.activateTask(activeTaskBean);

        verify(tasksRepository).activateTask(activeTaskBean);
        verify(tasksFragment).showMessage(MessageMap.ACTIVE);
    }
}