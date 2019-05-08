package com.codearms.maoqiqi.app.tasks;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.schedulers.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link TasksPresenter}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
public class TasksPresenterTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private TasksContract.View tasksView;

    @Captor
    private ArgumentCaptor<List<TaskBean>> listArgumentCaptor;

    private TasksPresenter tasksPresenter;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // The presenter won't update the view unless it's active.
        when(tasksView.isActive()).thenReturn(true);

        tasksPresenter = new TasksPresenter(tasksRepository, tasksView, new ImmediateSchedulerProvider());

        taskBeanList = new ArrayList<>();
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, true));
    }

    @Test
    public void setPresenter() {
        // The presenter is set to the view
        verify(tasksView).setPresenter(tasksPresenter);
    }

    @Test
    public void loadTasks() {
        when(tasksRepository.loadTasks()).thenReturn(Single.just(taskBeanList));

        // When loading of Tasks is requested
        tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        tasksPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(tasksRepository).loadTasks();

        // Then progress indicator is shown
        InOrder inOrder = Mockito.inOrder(tasksView);
        inOrder.verify(tasksView).setLoadingIndicator(true);

        // Then progress indicator is hidden and all tasks are shown in UI
        inOrder.verify(tasksView).setLoadingIndicator(false);
        verify(tasksView).showTasks(listArgumentCaptor.capture());
        assertEquals(listArgumentCaptor.getValue().size(), 3);
    }

    @Test
    public void loadActiveTasks() {
        when(tasksRepository.loadTasks()).thenReturn(Single.just(taskBeanList));

        tasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
        tasksPresenter.loadTasks(true);

        verify(tasksRepository).loadTasks();

        verify(tasksView).setLoadingIndicator(false);
        verify(tasksView).showTasks(listArgumentCaptor.capture());
        assertEquals(listArgumentCaptor.getValue().size(), 2);
    }

    @Test
    public void loadCompletedTasks() {
        when(tasksRepository.loadTasks()).thenReturn(Single.just(taskBeanList));

        tasksPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
        tasksPresenter.loadTasks(true);

        verify(tasksRepository).loadTasks();

        verify(tasksView).setLoadingIndicator(false);
        verify(tasksView).showTasks(listArgumentCaptor.capture());
        assertEquals(listArgumentCaptor.getValue().size(), 1);
    }

    @Test
    public void showNoTasks() {
        when(tasksRepository.loadTasks()).thenReturn(Single.<List<TaskBean>>error(new Exception()));

        tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        tasksPresenter.loadTasks(true);

        // And the tasks aren't available in the repository
        verify(tasksRepository).loadTasks();

        // Then an error message is shown
        verify(tasksView).showNoTasks();
    }

    @Test
    public void clearCompletedTasks() {
        when(tasksRepository.loadTasks()).thenReturn(Single.just(taskBeanList));

        // When completed tasks are cleared
        tasksPresenter.clearCompletedTasks();

        // Then repository is called and the view is notified
        verify(tasksRepository).clearCompletedTasks();
        verify(tasksRepository).loadTasks();
    }

    @Test
    public void completeTask() {
        // Given a stubbed activated task
        TaskBean completedTaskBean = new TaskBean(TITLE, DESCRIPTION, false);

        when(tasksRepository.loadTasks()).thenReturn(Single.just(Collections.singletonList(completedTaskBean)));

        // When task is marked as complete
        tasksPresenter.completeTask(completedTaskBean);

        // Then repository is called and task marked complete UI is shown
        verify(tasksRepository).completeTask(completedTaskBean);
        verify(tasksView).showMessage(MessageMap.COMPLETE);
    }

    @Test
    public void activateTask() {
        // Given a stubbed completed task
        TaskBean activeTaskBean = new TaskBean(TITLE, DESCRIPTION, true);

        when(tasksRepository.loadTasks()).thenReturn(Single.just(Collections.singletonList(activeTaskBean)));

        // When task is marked as activated
        tasksPresenter.activateTask(activeTaskBean);

        verify(tasksRepository).activateTask(activeTaskBean);
        verify(tasksView).showMessage(MessageMap.ACTIVE);
    }
}