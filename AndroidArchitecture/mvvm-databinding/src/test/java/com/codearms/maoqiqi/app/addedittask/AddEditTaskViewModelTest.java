package com.codearms.maoqiqi.app.addedittask;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link AddEditTaskViewModel}.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class AddEditTaskViewModelTest {

    private final static String ID = "ID";
    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private AddEditTaskFragment addEditTaskFragment;

    @Captor
    private ArgumentCaptor<TasksDataSource.GetTaskCallBack> getTaskCallBackArgumentCaptor;

    private AddEditTaskViewModel addEditTaskViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addTask() {
        addEditTaskViewModel = new AddEditTaskViewModel(null, tasksRepository);
        addEditTaskViewModel.setAddEditTaskListener(addEditTaskFragment);

        // When the ViewModel is asked to save a task
        addEditTaskViewModel.observableTitle.set(TITLE);
        addEditTaskViewModel.observableDescription.set(DESCRIPTION);
        addEditTaskViewModel.addTask();

        // Then a task is saved in the repository and the view updated
        verify(tasksRepository).addTask(Mockito.any(TaskBean.class));
        verify(addEditTaskFragment).showTasks();
    }

    @Test
    public void addTaskError() {
        addEditTaskViewModel = new AddEditTaskViewModel(null, tasksRepository);
        addEditTaskViewModel.setCallBack(addEditTaskFragment);

        // When the presenter is asked to save an empty task
        addEditTaskViewModel.observableTitle.set("");
        addEditTaskViewModel.observableDescription.set("");
        addEditTaskViewModel.addTask();

        // Then an empty not error is shown in the UI
        verify(addEditTaskFragment).showMessage(MessageMap.ENTER);
    }

    @Test
    public void addTaskExisting() {
        addEditTaskViewModel = new AddEditTaskViewModel(ID, tasksRepository);
        addEditTaskViewModel.setAddEditTaskListener(addEditTaskFragment);

        // When the presenter is asked to save an existing task
        addEditTaskViewModel.observableTitle.set(TITLE);
        addEditTaskViewModel.observableDescription.set(DESCRIPTION);
        addEditTaskViewModel.addTask();

        verify(tasksRepository).updateTask(Mockito.any(TaskBean.class));
        verify(addEditTaskFragment).showTasks();
    }

    @Test
    public void getTask() {
        TaskBean taskBean = new TaskBean(TITLE, DESCRIPTION, false);

        addEditTaskViewModel = new AddEditTaskViewModel(taskBean.getId(), tasksRepository);

        // When the ViewModel is asked to getTask an existing task
        addEditTaskViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(taskBean.getId()), getTaskCallBackArgumentCaptor.capture());
        assertTrue(addEditTaskViewModel.isDataMissing);

        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(taskBean);

        assertEquals(addEditTaskViewModel.observableTitle.get(), taskBean.getTitle());
        assertEquals(addEditTaskViewModel.observableDescription.get(), taskBean.getDescription());

        assertFalse(addEditTaskViewModel.isDataMissing);
    }
}