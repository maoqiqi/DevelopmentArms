package com.codearms.maoqiqi.app.addedittask;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;

import org.junit.Before;
import org.junit.Rule;
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

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;

    @Captor
    private ArgumentCaptor<TasksDataSource.GetTaskCallBack> getTaskCallBackArgumentCaptor;

    private AddEditTaskViewModel addEditTaskViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        addEditTaskViewModel = new AddEditTaskViewModel(tasksRepository);
    }

    @Test
    public void addTask() {
        // When the ViewModel is asked to save a task
        addEditTaskViewModel.observableTitle.setValue(TITLE);
        addEditTaskViewModel.observableDescription.setValue(DESCRIPTION);
        addEditTaskViewModel.addTask();

        // Then a task is saved in the repository and the view updated
        verify(tasksRepository).addTask(Mockito.any(TaskBean.class));
    }

    @Test
    public void getTask() {
        TaskBean taskBean = new TaskBean(TITLE, DESCRIPTION, false);

        addEditTaskViewModel.setTaskId(taskBean.getId());

        // When the ViewModel is asked to getTask an existing task
        addEditTaskViewModel.start();

        verify(tasksRepository).getTask(Matchers.eq(taskBean.getId()), getTaskCallBackArgumentCaptor.capture());
        assertTrue(addEditTaskViewModel.isDataMissing);

        getTaskCallBackArgumentCaptor.getValue().onTaskLoaded(taskBean);

        assertEquals(addEditTaskViewModel.observableTitle.getValue(), taskBean.getTitle());
        assertEquals(addEditTaskViewModel.observableDescription.getValue(), taskBean.getDescription());

        assertFalse(addEditTaskViewModel.isDataMissing);
    }
}