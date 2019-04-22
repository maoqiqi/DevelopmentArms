package com.codearms.maoqiqi.app.statistics;

import com.codearms.maoqiqi.app.base.BasePresenter;
import com.codearms.maoqiqi.app.base.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
public interface StatisticsContract {

    interface Presenter extends BasePresenter {

        void loadStatistics();
    }

    interface View extends BaseView<Presenter> {

        void showStatistics(int numberOfActiveTasks, int numberOfCompletedTasks);

        void showMessage(String message);
    }
}