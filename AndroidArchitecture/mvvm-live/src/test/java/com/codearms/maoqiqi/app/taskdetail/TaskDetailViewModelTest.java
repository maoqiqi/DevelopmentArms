package com.codearms.maoqiqi.app.taskdetail;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.LiveDataTestUtils;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskDetailViewModel}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class TaskDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;

    @Captor
    private ArgumentCaptor<TasksDataSource.GetTaskCallBack> getTaskCallBackArgumentCaptor;

    private TaskDetailViewModel taskDetailViewModel;

    private TaskBean activeTaskBean;
    private TaskBean completedTaskBean;
    private TaskBean unknownTaskBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        taskDetailViewModel = new TaskDetailViewModel(tasksRepository);

        activeTaskBean = new TaskBean(TITLE, DESCRIPTION, false);
        completedTaskBean = new TaskBean(TITLE, DESCRIPTION, true);
        unknownTaskBean = new TaskBean("", TITLE, DESCRIPTION, false);
    }

    @Test
    public void getActiveTask() {
        // When tasks presenter is asked to open a task
        taskDetailViewModel.setTaskId(activeTaskBean.getId());
        taskDetailViewModel.start();

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        // Trigger callback
        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(activeTaskBean);

        // Then verify that the view was notified
        assertEquals(taskDetailViewModel.observableTitle.getValue(), activeTaskBean.getTitle());
        assertEquals(taskDetailViewModel.observableDescription.getValue(), activeTaskBean.getDescription());
    }

    @Test
    public void getCompletedTask() {
        taskDetailViewModel.setTaskId(completedTaskBean.getId());
        taskDetailViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(completedTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(completedTaskBean);

        assertEquals(taskDetailViewModel.observableTitle.getValue(), completedTaskBean.getTitle());
        assertEquals(taskDetailViewModel.observableDescription.getValue(), completedTaskBean.getDescription());
    }

    @Test
    public void getUnknownTask() throws InterruptedException {
        // When loading of a task is requested with an invalid task ID.
        taskDetailViewModel.setTaskId(unknownTaskBean.getId());
        taskDetailViewModel.start();
        Event<String> event = LiveDataTestUtils.getValue(taskDetailViewModel.getMessage());
        assertEquals(event.getContent(), MessageMap.NO_ID);
    }

    @Test
    public void getTaskNotAvailable() throws InterruptedException {
        taskDetailViewModel.setTaskId(activeTaskBean.getId());
        taskDetailViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onDataNotAvailable();

        Event<String> event = LiveDataTestUtils.getValue(taskDetailViewModel.getMessage());
        assertEquals(event.getContent(), MessageMap.NO_DATA);
    }

    @Test
    public void completeTask() {
        // Given an initialized presenter with an active task
        taskDetailViewModel.setTaskId(activeTaskBean.getId());

        // When the presenter is asked to complete the task
        taskDetailViewModel.completeTask();

        // Then a request is sent to the task repository and the UI is updated
        verify(tasksRepository).completeTask(activeTaskBean.getId());
    }

    @Test
    public void activateTask() {
        // Given an initialized presenter with a completed task
        taskDetailViewModel.setTaskId(completedTaskBean.getId());

        // When the presenter is asked to activate the task
        taskDetailViewModel.activateTask();

        verify(tasksRepository).activateTask(completedTaskBean.getId());
    }

    @Test
    public void deleteTask() {
        // When the deletion of a task is requested
        taskDetailViewModel.setTaskId(activeTaskBean.getId());

        taskDetailViewModel.deleteTask();

        // Then the repository and the view are notified
        verify(tasksRepository).deleteTask(activeTaskBean.getId());
    }
}