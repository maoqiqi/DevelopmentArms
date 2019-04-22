package com.codearms.maoqiqi.app.tasks;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the data to be used in the tasks screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 11:42
 */
public class TasksViewModel extends ViewModel {

    private final TasksRepository tasksRepository;

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private boolean firstLoad = true;

    // These observable fields will update Views automatically
    public final MutableLiveData<Boolean> observableLoading = new MutableLiveData<>();
    public final MutableLiveData<Integer> observableFilteringLabel = new MutableLiveData<>();
    public final MutableLiveData<List<TaskBean>> observableList = new MutableLiveData<>();
    public final MutableLiveData<Boolean> observableNoTasks = new MutableLiveData<>();
    public final MutableLiveData<Integer> observableNoTasksRes = new MutableLiveData<>();
    public final MutableLiveData<Integer> observableNoTasksText = new MutableLiveData<>();

    private final MutableLiveData<Event<String>> taskItemEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> message = new MutableLiveData<>();

    MutableLiveData<Event<String>> getTaskItemEvent() {
        return taskItemEvent;
    }

    MutableLiveData<Event<String>> getMessage() {
        return message;
    }

    public TasksViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;

        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS);
    }

    void start() {
        loadTasks(false);
    }

    void loadTasks(boolean forceUpdate) {
        // The first load forcing update
        loadTasks(forceUpdate || firstLoad, true);
        firstLoad = false;
    }

    /**
     * load tasks
     *
     * @param forceUpdate Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoading Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoading) {
        if (showLoading) observableLoading.setValue(true);

        // Notice:Tests are used to clear the data each time.
        // if (forceUpdate) tasksRepository.refreshTasks();

        tasksRepository.loadTasks(new TasksDataSource.LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<TaskBean> taskBeanList) {
                List<TaskBean> tasksToShow = new ArrayList<>();

                // We filter the tasks based on the requestType
                for (TaskBean taskBean : taskBeanList) {
                    switch (currentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(taskBean);
                            break;
                        case ACTIVE_TASKS:
                            if (taskBean.isActive()) tasksToShow.add(taskBean);
                            break;
                        case COMPLETED_TASKS:
                            if (taskBean.isCompleted()) tasksToShow.add(taskBean);
                            break;
                    }
                }
                if (showLoading) observableLoading.setValue(false);

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (showLoading) observableLoading.setValue(false);
                // Show a message indicating there are no tasks for that filter type.
                observableNoTasks.setValue(true);
            }
        });
    }

    private void processTasks(List<TaskBean> taskBeanList) {
        if (taskBeanList.isEmpty()) {
            observableNoTasks.setValue(true);
        } else {
            observableNoTasks.setValue(false);
            observableList.setValue(taskBeanList);
        }
    }

    void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        message.setValue(new Event<>(MessageMap.CLEAR));
        loadTasks(false, false);
    }

    public void completeTask(TaskBean completedTaskBean) {
        tasksRepository.completeTask(completedTaskBean);
        message.setValue(new Event<>(MessageMap.COMPLETE));
        loadTasks(false, false);
    }

    public void activateTask(TaskBean activeTaskBean) {
        tasksRepository.activateTask(activeTaskBean);
        message.setValue(new Event<>(MessageMap.ACTIVE));
        loadTasks(false, false);
    }

    public void openTaskDetails(TaskBean requestedTaskBean) {
        taskItemEvent.setValue(new Event<>(requestedTaskBean.getId()));
    }

    void result(int requestCode, int resultCode) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            switch (resultCode) {
                case TasksActivity.RESULT_ADD_TASK:
                    message.setValue(new Event<>(MessageMap.ADD));
                    break;
                case TasksActivity.RESULT_EDIT_TASK:
                    message.setValue(new Event<>(MessageMap.EDIT));
                    break;
                case TasksActivity.RESULT_DELETE_TASK:
                    message.setValue(new Event<>(MessageMap.DELETE));
                    break;
            }
        }
    }

    /**
     * Sets the current task filtering type.
     */
    void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (currentFiltering) {
            case ALL_TASKS:
                observableFilteringLabel.setValue(R.string.all_tasks);
                observableNoTasksRes.setValue(R.drawable.ic_all_tasks);
                observableNoTasksText.setValue(R.string.no_all_tasks);
                break;
            case ACTIVE_TASKS:
                observableFilteringLabel.setValue(R.string.active_tasks);
                observableNoTasksRes.setValue(R.drawable.ic_active_tasks);
                observableNoTasksText.setValue(R.string.no_active_tasks);
                break;
            case COMPLETED_TASKS:
                observableFilteringLabel.setValue(R.string.completed_tasks);
                observableNoTasksRes.setValue(R.drawable.ic_completed_tasks);
                observableNoTasksText.setValue(R.string.no_completed_tasks);
                break;
        }
    }
}