package com.codearms.maoqiqi.app.statistics

import com.codearms.maoqiqi.app.base.BasePresenter
import com.codearms.maoqiqi.app.base.BaseView

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
interface StatisticsContract {

    interface Presenter : BasePresenter {

        fun loadStatistics()
    }

    interface View : BaseView<Presenter> {

        fun showStatistics(numberOfActiveTasks: Int, numberOfCompletedTasks: Int)

        fun showMessage(message: String)
    }
}