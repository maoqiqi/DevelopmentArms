package com.codearms.maoqiqi.app.addedittask;

import com.codearms.maoqiqi.app.base.BasePresenter;
import com.codearms.maoqiqi.app.base.BaseView;
import com.codearms.maoqiqi.app.data.TaskBean;

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
public interface AddEditTaskContract {

    interface Presenter extends BasePresenter {

        void getTask();

        boolean isDataMissing();

        void addTask(String title, String description);
    }

    interface View extends BaseView<Presenter> {

        void setData(TaskBean taskBean);

        void showTasks();

        void showMessage(String message);

        String getTitle();

        String getDescription();
    }
}