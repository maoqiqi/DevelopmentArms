package com.codearms.maoqiqi.app.taskdetail;

import com.codearms.maoqiqi.app.base.BasePresenter;
import com.codearms.maoqiqi.app.base.BaseView;
import com.codearms.maoqiqi.app.data.TaskBean;

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
public interface TaskDetailContract {

    interface Presenter extends BasePresenter {

        void getTask();

        void completeTask();

        void activateTask();

        void deleteTask();
    }

    interface View extends BaseView<Presenter> {

        void showTask(TaskBean taskBean);

        void deleteTask();

        void editTask(String taskId);

        void showMessage(String message);
    }
}