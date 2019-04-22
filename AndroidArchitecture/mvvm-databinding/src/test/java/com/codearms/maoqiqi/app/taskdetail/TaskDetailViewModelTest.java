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
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskDetailViewModel}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class TaskDetailViewModelTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private TaskDetailFragment taskDetailFragment;

    @Captor
    private ArgumentCaptor<TasksDataSource.GetTaskCallBack> getTaskCallBackArgumentCaptor;

    private TaskDetailViewModel taskDetailViewModel;

    private TaskBean activeTaskBean;
    private TaskBean completedTaskBean;
    private TaskBean unknownTaskBean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        activeTaskBean = new TaskBean(TITLE, DESCRIPTION, false);
        completedTaskBean = new TaskBean(TITLE, DESCRIPTION, true);
        unknownTaskBean = new TaskBean("", TITLE, DESCRIPTION, false);
    }

    @Test
    public void getActiveTask() {
        // When tasks presenter is asked to open a task
        taskDetailViewModel = new TaskDetailViewModel(activeTaskBean.getId(), tasksRepository);
        taskDetailViewModel.start();

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        // Trigger callback
        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(activeTaskBean);

        // Then verify that the view was notified
        assertEquals(taskDetailViewModel.observableTitle.get(), activeTaskBean.getTitle());
        assertEquals(taskDetailViewModel.observableDescription.get(), activeTaskBean.getDescription());
    }

    @Test
    public void getCompletedTask() {
        taskDetailViewModel = new TaskDetailViewModel(completedTaskBean.getId(), tasksRepository);
        taskDetailViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(completedTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(completedTaskBean);

        assertEquals(taskDetailViewModel.observableTitle.get(), completedTaskBean.getTitle());
        assertEquals(taskDetailViewModel.observableDescription.get(), completedTaskBean.getDescription());
    }

    @Test
    public void getUnknownTask() {
        // When loading of a task is requested with an invalid task ID.
        taskDetailViewModel = new TaskDetailViewModel(unknownTaskBean.getId(), tasksRepository);
        taskDetailViewModel.setCallBack(taskDetailFragment);
        taskDetailViewModel.start();
        verify(taskDetailFragment).showMessage(MessageMap.NO_ID);
    }

    @Test
    public void getTaskNotAvailable() {
        taskDetailViewModel = new TaskDetailViewModel(activeTaskBean.getId(), tasksRepository);
        taskDetailViewModel.setCallBack(taskDetailFragment);
        taskDetailViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(activeTaskBean.getId()), getTaskCallBackArgumentCaptor.capture());

        getTaskCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(taskDetailFragment).showMessage(MessageMap.NO_DATA);
    }

    @Test
    public void completeTask() {
        // Given an initialized presenter with an active task
        taskDetailViewModel = new TaskDetailViewModel(activeTaskBean.getId(), tasksRepository);
        taskDetailViewModel.setCallBack(taskDetailFragment);

        // When the presenter is asked to complete the task
        taskDetailViewModel.completeTask();

        // Then a request is sent to the task repository and the UI is updated
        verify(tasksRepository).completeTask(activeTaskBean.getId());
        verify(taskDetailFragment).showMessage(MessageMap.COMPLETE);
    }

    @Test
    public void activateTask() {
        // Given an initialized presenter with a completed task
        taskDetailViewModel = new TaskDetailViewModel(completedTaskBean.getId(), tasksRepository);
        taskDetailViewModel.setCallBack(taskDetailFragment);

        // When the presenter is asked to activate the task
        taskDetailViewModel.activateTask();

        verify(tasksRepository).activateTask(completedTaskBean.getId());
        verify(taskDetailFragment).showMessage(MessageMap.ACTIVE);
    }

    @Test
    public void deleteTask() {
        // When the deletion of a task is requested
        taskDetailViewModel = new TaskDetailViewModel(activeTaskBean.getId(), tasksRepository);
        taskDetailViewModel.setTaskDetailListener(taskDetailFragment);
        taskDetailViewModel.deleteTask();

        // Then the repository and the view are notified
        verify(tasksRepository).deleteTask(activeTaskBean.getId());
        verify(taskDetailFragment).deleteTask();
    }
}