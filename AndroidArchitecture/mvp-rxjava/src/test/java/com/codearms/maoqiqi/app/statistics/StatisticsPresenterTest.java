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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StatisticsPresenter}
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 11:25
 */
public class StatisticsPresenterTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    @Mock
    private TasksRepository tasksRepository;
    @Mock
    private StatisticsContract.View statisticsView;

    private StatisticsPresenter statisticsPresenter;

    private List<TaskBean> taskBeanList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(statisticsView.isActive()).thenReturn(true);

        statisticsPresenter = new StatisticsPresenter(tasksRepository, statisticsView);

        taskBeanList = new ArrayList<>();
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, false));
        taskBeanList.add(new TaskBean(TITLE, DESCRIPTION, true));
    }

    @Test
    public void setPresenter() {
        verify(statisticsView).setPresenter(statisticsPresenter);
    }

    @Test
    public void loadStatisticsEmpty() {
        // Given an initialized StatisticsPresenter with no tasks
        taskBeanList.clear();

        statisticsPresenter.subscribe();

        verify(tasksRepository).loadTasks();

//        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        verify(statisticsView).showStatistics(0, 0);
    }

    @Test
    public void loadStatistics() {
        statisticsPresenter.subscribe();

        verify(tasksRepository).loadTasks();

//        loadTasksCallBackArgumentCaptor.getValue().onTasksLoaded(taskBeanList);

        verify(statisticsView).showStatistics(2, 1);
    }

    @Test
    public void loadStatisticsUnavailable() {
        statisticsPresenter.subscribe();

        verify(tasksRepository).loadTasks();

//        loadTasksCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(statisticsView).showMessage(MessageMap.NO_DATA);
    }
}