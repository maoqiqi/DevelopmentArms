package com.codearms.maoqiqi.app.tasks;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens to user actions from the UI ({@link TasksFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository tasksRepository;
    private final TasksContract.View tasksView;

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private boolean firstLoad = true;

    TasksPresenter(TasksRepository tasksRepository, TasksContract.View tasksView) {
        this.tasksRepository = tasksRepository;
        this.tasksView = tasksView;
        this.tasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
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
        if (showLoading) tasksView.setLoadingIndicator(true);

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

                // The view may not be able to handle UI updates anymore
                if (!tasksView.isActive()) return;

                if (showLoading) tasksView.setLoadingIndicator(false);

                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!tasksView.isActive()) return;

                if (showLoading) tasksView.setLoadingIndicator(false);
                tasksView.showNoTasks();
            }
        });
    }

    private void processTasks(List<TaskBean> taskBeanList) {
        if (taskBeanList.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            tasksView.showNoTasks();
        } else {
            // Set the filter label's text.
            tasksView.showFilterLabel();
            // Show the list of tasks
            tasksView.showTasks(taskBeanList);
        }
    }

    @Override
    public void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        tasksView.showMessage(MessageMap.CLEAR);
        loadTasks(false, false);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        tasksRepository.completeTask(completedTaskBean);
        tasksView.showMessage(MessageMap.COMPLETE);
        loadTasks(false, false);
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        tasksRepository.activateTask(activeTaskBean);
        tasksView.showMessage(MessageMap.ACTIVE);
        loadTasks(false, false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            switch (resultCode) {
                case TasksActivity.RESULT_ADD_TASK:
                    tasksView.showMessage(MessageMap.ADD);
                    break;
                case TasksActivity.RESULT_EDIT_TASK:
                    tasksView.showMessage(MessageMap.EDIT);
                    break;
                case TasksActivity.RESULT_DELETE_TASK:
                    tasksView.showMessage(MessageMap.DELETE);
                    break;
            }
        }
    }

    /**
     * Sets the current task filtering type.
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;
    }

    /**
     * Gets the current task filtering type.
     */
    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }
}