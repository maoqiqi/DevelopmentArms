package com.codearms.maoqiqi.app.statistics;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StatisticsViewModel}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/19 10:18
 */
public class StatisticsViewModelTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private StatisticsFragment statisticsFragment;

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

        assertEquals(statisticsViewModel.observableActiveTasks.get(), 0);
        assertEquals(statisticsViewModel.observableCompletedTasks.get(), 0);
    }

    @Test
    public void loadStatistics() {
        statisticsViewModel.start();

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        assertEquals(statisticsViewModel.observableActiveTasks.get(), 2);
        assertEquals(statisticsViewModel.observableCompletedTasks.get(), 1);
    }

    @Test
    public void loadStatisticsError() {
        statisticsViewModel.start();
        statisticsViewModel.setCallBack(statisticsFragment);

        verify(tasksRepository).loadTasks(loadTasksCallBackArgumentCaptor.capture());

        loadTasksCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(statisticsFragment).showMessage(MessageMap.NO_DATA);
    }
}