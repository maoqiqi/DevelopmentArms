package com.codearms.maoqiqi.app.tasks

import com.codearms.maoqiqi.app.base.BasePresenter
import com.codearms.maoqiqi.app.base.BaseView
import com.codearms.maoqiqi.app.data.TaskBean

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
interface TasksContract {

    interface Presenter : BasePresenter {

        var filtering: TasksFilterType

        fun loadTasks(forceUpdate: Boolean)

        fun clearCompletedTasks()

        fun completeTask(completedTaskBean: TaskBean)

        fun activateTask(activeTaskBean: TaskBean)

        fun result(requestCode: Int, resultCode: Int)
    }

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(showLoading: Boolean)

        fun showFilterLabel()

        fun showTasks(taskBeanList: List<TaskBean>)

        fun showNoTasks()

        fun showFilteringPopUpMenu()

        fun openTaskDetails(taskId: String)

        fun addTask()

        fun showMessage(message: String)
    }
}