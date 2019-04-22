package com.codearms.maoqiqi.app.statistics;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import java.util.List;

/**
 * Listens to user actions from the UI ({@link StatisticsFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private TasksRepository tasksRepository;
    private StatisticsContract.View statisticsView;

    StatisticsPresenter(TasksRepository tasksRepository, StatisticsContract.View statisticsView) {
        this.tasksRepository = tasksRepository;
        this.statisticsView = statisticsView;
        this.statisticsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStatistics();
    }

    @Override
    public void loadStatistics() {
        tasksRepository.loadTasks(new TasksDataSource.LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<TaskBean> taskBeanList) {
                int activeTasks = 0;
                int completedTasks = 0;

                // We calculate number of active and completed tasks
                for (TaskBean taskBean : taskBeanList) {
                    if (taskBean.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }

                if (statisticsView.isActive())
                    statisticsView.showStatistics(activeTasks, completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                if (statisticsView.isActive()) statisticsView.showMessage(MessageMap.NO_DATA);
            }
        });
    }
}