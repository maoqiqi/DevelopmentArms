package com.codearms.maoqiqi.app.tasks;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.codearms.maoqiqi.app.CallBack;
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
public class TasksViewModel extends BaseObservable {

    private final TasksRepository tasksRepository;

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private boolean firstLoad = true;

    // These observable fields will update Views automatically
    public final ObservableBoolean observableLoading = new ObservableBoolean();
    public final ObservableInt observableFilteringLabel = new ObservableInt();
    public final ObservableList<TaskBean> observableList = new ObservableArrayList<>();
    public final ObservableBoolean observableNoTasks = new ObservableBoolean();
    public final ObservableInt observableNoTasksRes = new ObservableInt();
    public final ObservableInt observableNoTasksText = new ObservableInt();

    private TaskItemListener taskItemListener;
    private CallBack callBack;

    void setTaskItemListener(TaskItemListener taskItemListener) {
        this.taskItemListener = taskItemListener;
    }

    void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    TasksViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;

        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS);
    }

    void destroy() {
        // Clear references to avoid potential memory leaks.
        taskItemListener = null;
        callBack = null;
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
        if (showLoading) observableLoading.set(true);

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
                if (showLoading) observableLoading.set(false);

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (showLoading) observableLoading.set(false);
                // Show a message indicating there are no tasks for that filter type.
                observableNoTasks.set(true);
            }
        });
    }

    private void processTasks(List<TaskBean> taskBeanList) {
        if (taskBeanList.isEmpty()) {
            observableNoTasks.set(true);
        } else {
            observableNoTasks.set(false);
            observableList.clear();
            observableList.addAll(taskBeanList);
        }
    }

    void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        if (callBack != null) callBack.showMessage(MessageMap.CLEAR);
        loadTasks(false, false);
    }

    public void completeTask(TaskBean completedTaskBean) {
        tasksRepository.completeTask(completedTaskBean);
        if (callBack != null) callBack.showMessage(MessageMap.COMPLETE);
        loadTasks(false, false);
    }

    public void activateTask(TaskBean activeTaskBean) {
        tasksRepository.activateTask(activeTaskBean);
        if (callBack != null) callBack.showMessage(MessageMap.ACTIVE);
        loadTasks(false, false);
    }

    public void openTaskDetails(TaskBean requestedTaskBean) {
        if (taskItemListener != null) taskItemListener.onOpenTaskDetails(requestedTaskBean.getId());
    }

    void result(int requestCode, int resultCode) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            switch (resultCode) {
                case TasksActivity.RESULT_ADD_TASK:
                    if (callBack != null) callBack.showMessage(MessageMap.ADD);
                    break;
                case TasksActivity.RESULT_EDIT_TASK:
                    if (callBack != null) callBack.showMessage(MessageMap.EDIT);
                    break;
                case TasksActivity.RESULT_DELETE_TASK:
                    if (callBack != null) callBack.showMessage(MessageMap.DELETE);
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
                observableFilteringLabel.set(R.string.all_tasks);
                observableNoTasksRes.set(R.drawable.ic_all_tasks);
                observableNoTasksText.set(R.string.no_all_tasks);
                break;
            case ACTIVE_TASKS:
                observableFilteringLabel.set(R.string.active_tasks);
                observableNoTasksRes.set(R.drawable.ic_active_tasks);
                observableNoTasksText.set(R.string.no_active_tasks);
                break;
            case COMPLETED_TASKS:
                observableFilteringLabel.set(R.string.completed_tasks);
                observableNoTasksRes.set(R.drawable.ic_completed_tasks);
                observableNoTasksText.set(R.string.no_completed_tasks);
                break;
        }
    }
}