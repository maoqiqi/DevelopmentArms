package com.codearms.maoqiqi.app.statistics;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableInt;

import com.codearms.maoqiqi.app.CallBack;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import java.util.List;

/**
 * Exposes the data to be used in the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 13:28
 */
public class StatisticsViewModel extends BaseObservable {

    private final TasksRepository tasksRepository;

    public final ObservableInt observableActiveTasks = new ObservableInt();
    public final ObservableInt observableCompletedTasks = new ObservableInt();

    private CallBack callBack;

    void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    StatisticsViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    void destroy() {
        callBack = null;
    }

    void start() {
        loadStatistics();
    }

    private void loadStatistics() {
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

                observableActiveTasks.set(activeTasks);
                observableCompletedTasks.set(completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                if (callBack != null) callBack.showMessage(MessageMap.NO_DATA);
            }
        });
    }
}