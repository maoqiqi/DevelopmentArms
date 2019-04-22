package com.codearms.maoqiqi.app.taskdetail;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskDetailPresenter}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
public class TaskDetailPresenterTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private TaskDetailContract.View taskDetailView;

    @Captor
    private ArgumentCaptor<TasksDataSource.GetTaskCallBack> getTaskCallBackArgumentCaptor;

    private TaskDetailPresenter taskDetailPresenter;

    private TaskBean activeTaskBean;
    private TaskBean completedTaskBean;
    private TaskBean unknownTaskBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(taskDetailView.isActive()).thenReturn(true);

        activeTaskBean = new TaskBean(TITLE, DESCRIPTION, false);
        completedTaskBean = new TaskBean(TITLE, DESCRIPTION, true);
        unknownTaskBean = new TaskBean("", TITLE, DESCRIPTION, false);
    }

    @Test
    public void setPresenter() {
        taskDetailPresenter = new TaskDetailPresenter(activeTaskBean.getId(), tasksRepository, taskDetailView);
        verify(taskDetailView).setPresenter(taskDetailPresenter);
    }

    @Test
    public void getActiveTask() {
        // When tasks presenter is asked to open a task
        taskDetailPresenter = new TaskDetailPresenter(activeTaskBean.getId(), tasksRepository, taskDetailView);
        taskDetailPresenter.start();

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        // Trigger callback
        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(activeTaskBean);

        // Title, description and completion status are shown in UI
        verify(taskDetailView).showTask(activeTaskBean);
    }

    @Test
    public void getCompletedTask() {
        taskDetailPresenter = new TaskDetailPresenter(completedTaskBean.getId(), tasksRepository, taskDetailView);
        taskDetailPresenter.start();

        verify(tasksRepository).getTask(Matchers.eq(completedTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(completedTaskBean);

        verify(taskDetailView).showTask(completedTaskBean);
    }

    @Test
    public void getUnknownTask() {
        // When loading of a task is requested with an invalid task ID.
        taskDetailPresenter = new TaskDetailPresenter(unknownTaskBean.getId(), tasksRepository, taskDetailView);
        taskDetailPresenter.start();
        verify(taskDetailView).showMessage(MessageMap.NO_ID);
    }

    @Test
    public void getTaskNotAvailable() {
        taskDetailPresenter = new TaskDetailPresenter(activeTaskBean.getId(), tasksRepository, taskDetailView);
        taskDetailPresenter.start();

        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(taskDetailView).showMessage(MessageMap.NO_DATA);
    }

    @Test
    public void completeTask() {
        // Given an initialized presenter with an active task
        taskDetailPresenter = new TaskDetailPresenter(activeTaskBean.getId(), tasksRepository, taskDetailView);

        // When the presenter is asked to complete the task
        taskDetailPresenter.completeTask();

        // Then a request is sent to the task repository and the UI is updated
        verify(tasksRepository).completeTask(activeTaskBean.getId());
        verify(taskDetailView).showMessage(MessageMap.COMPLETE);
    }

    @Test
    public void activateTask() {
        // Given an initialized presenter with a completed task
        taskDetailPresenter = new TaskDetailPresenter(completedTaskBean.getId(), tasksRepository, taskDetailView);

        // When the presenter is asked to activate the task
        taskDetailPresenter.activateTask();

        verify(tasksRepository).activateTask(completedTaskBean.getId());
        verify(taskDetailView).showMessage(MessageMap.ACTIVE);
    }

    @Test
    public void deleteTask() {
        // When the deletion of a task is requested
        taskDetailPresenter = new TaskDetailPresenter(activeTaskBean.getId(), tasksRepository, taskDetailView);
        taskDetailPresenter.deleteTask();

        // Then the repository and the view are notified
        verify(tasksRepository).deleteTask(activeTaskBean.getId());
        verify(taskDetailView).deleteTask();
    }
}