package com.codearms.maoqiqi.app.statistics;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StatisticsViewModel}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class StatisticsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;

    @Captor
    private ArgumentCaptor<TasksDataSource.LoadTasksCallBack> loadTasksCallBackArgumentCaptor;

    private StatisticsViewModel statisticsViewModel;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        statisticsViewModel = new StatisticsViewModel(tasksRepository);

        taskBeanList = new ArrayList<>();
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, true));
    }

    @Test
    public void loadStatisticsEmpty() {
        // Given an initialized StatisticsViewModel with no tasks
        taskBeanList.clear();

        statisticsViewModel.start();

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        assertThat(statisticsViewModel.observableActiveTasks.getValue(), is(0));
        assertThat(statisticsViewModel.observableCompletedTasks.getValue(), is(0));
    }

    @Test
    public void loadStatistics() {
        statisticsViewModel.start();

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        assertThat(statisticsViewModel.observableActiveTasks.getValue(), is(2));
        assertThat(statisticsViewModel.observableCompletedTasks.getValue(), is(1));
    }

    @Test
    public void loadStatisticsError() throws InterruptedException {
        statisticsViewModel.start();

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        loadTasksCallBackArgumentCaptor.getValue().onDataNotAvailable();

        Event<String> event = LiveDataTestUtils.getValue(statisticsViewModel.getMessage());
        assertEquals(event.getContent(), MessageMap.NO_DATA);
    }
}