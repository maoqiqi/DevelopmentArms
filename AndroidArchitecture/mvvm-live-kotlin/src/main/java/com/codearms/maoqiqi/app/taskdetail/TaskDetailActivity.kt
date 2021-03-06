package com.codearms.maoqiqi.app.taskdetail

import android.os.Bundle
import android.view.View
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays task details screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class TaskDetailActivity : BaseActivity(), View.OnClickListener {

    private var taskId: String? = null
    private lateinit var taskDetailFragment: TaskDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Get the requested task id
        taskId = intent.getStringExtra(TasksActivity.EXTRA_TASK_ID)

        setToolbar(R.id.toolbar) {
            setTitle(R.string.task_detail)
            setDisplayHomeAsUpEnabled(true)
        }

        findViewById<View>(R.id.fabEditTask).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        taskDetailFragment.editTask(taskId!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}