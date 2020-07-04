package com.codearms.maoqiqi.app.addedittask;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link AddEditTaskPresenter}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
public class AddEditTaskPresenterTest {

    private final static String ID = "ID";
    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private AddEditTaskContract.View addEditTaskView;

    private AddEditTaskPresenter addEditTaskPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(addEditTaskView.isActive()).thenReturn(true);
    }

    @Test
    public void setPresenter() {
        addEditTaskPresenter = new AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true);
        verify(addEditTaskView).setPresenter(addEditTaskPresenter);
    }

    @Test
    public void addTask() {
        addEditTaskPresenter = new AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true);

        // When the presenter is asked to save a task
        addEditTaskPresenter.addTask(TITLE, DESCRIPTION);

        // Then a task is saved in the repository and the view updated
        verify(tasksRepository).addTask(Mockito.any(TaskBean.class));
        verify(addEditTaskView).showTasks();
    }

    @Test
    public void addTaskError() {
        addEditTaskPresenter = new AddEditTaskPresenter(null, tasksRepository, addEditTaskView, true);

        // When the presenter is asked to save an empty task
        addEditTaskPresenter.addTask("", "");

        // Then an empty not error is shown in the UI
        verify(addEditTaskView).showMessage(MessageMap.ENTER);
    }

    @Test
    public void addTaskExisting() {
        addEditTaskPresenter = new AddEditTaskPresenter(ID, tasksRepository, addEditTaskView, true);

        // When the presenter is asked to save an existing task
        addEditTaskPresenter.addTask(TITLE, DESCRIPTION);

        verify(tasksRepository).updateTask(Mockito.any(TaskBean.class));
        verify(addEditTaskView).showTasks();
    }

    @Test
    public void getTask() {
        TaskBean taskBean = new TaskBean(TITLE, DESCRIPTION, false);

        addEditTaskPresenter = new AddEditTaskPresenter(taskBean.getId(), tasksRepository, addEditTaskView, true);

        // When the presenter is asked to populate an existing task
        addEditTaskPresenter.subscribe();

        verify(tasksRepository).getTask(Mockito.eq(taskBean.getId()));
        assertTrue(addEditTaskPresenter.isDataMissing());

//        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(taskBean);

        verify(addEditTaskView).setData(taskBean);
        assertFalse(addEditTaskPresenter.isDataMissing());
    }
}