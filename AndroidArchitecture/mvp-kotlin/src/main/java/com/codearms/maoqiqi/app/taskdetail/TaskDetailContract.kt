package com.codearms.maoqiqi.app.taskdetail

import com.codearms.maoqiqi.app.base.BasePresenter
import com.codearms.maoqiqi.app.base.BaseView
import com.codearms.maoqiqi.app.data.TaskBean

/**
 * This specifies the contract between the view and the presenter.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:21
 */
interface TaskDetailContract {

    interface Presenter : BasePresenter {

        fun getTask()

        fun completeTask()

        fun activateTask()

        fun deleteTask()
    }

    interface View : BaseView<Presenter> {

        fun showTask(taskBean: TaskBean)

        fun deleteTask()

        fun editTask(taskId: String)

        fun showMessage(message: String)
    }
}