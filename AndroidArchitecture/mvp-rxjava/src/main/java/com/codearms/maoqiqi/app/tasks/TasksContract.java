package com.codearms.maoqiqi.app.tasks;

import com.codearms.maoqiqi.app.base.BasePresenter;
import com.codearms.maoqiqi.app.base.BaseView;
import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
public interface TasksContract {

    interface Presenter extends BasePresenter {

        void loadTasks(boolean forceUpdate);

        void clearCompletedTasks();

        void completeTask(TaskBean completedTaskBean);

        void activateTask(TaskBean activeTaskBean);

        void result(int requestCode, int resultCode);

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean showLoading);

        void showFilterLabel();

        void showTasks(List<TaskBean> taskBeanList);

        void showNoTasks();

        void showFilteringPopUpMenu();

        void openTaskDetails(String taskId);

        void addTask();

        void showMessage(String message);
    }
}