package com.codearms.maoqiqi.app.statistics;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codearms.maoqiqi.app.Event;
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
public class StatisticsViewModel extends ViewModel {

    private final TasksRepository tasksRepository;

    public final MutableLiveData<Integer> observableActiveTasks = new MutableLiveData<>();
    public final MutableLiveData<Integer> observableCompletedTasks = new MutableLiveData<>();

    private final MutableLiveData<Event<String>> message = new MutableLiveData<>();

    MutableLiveData<Event<String>> getMessage() {
        return message;
    }

    public StatisticsViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
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

                observableActiveTasks.setValue(activeTasks);
                observableCompletedTasks.setValue(completedTasks);
            }

            @Override
            public void onDataNotAvailable() {
                message.setValue(new Event<>(MessageMap.NO_DATA));
            }
        });
    }
}