package com.codearms.maoqiqi.app.addedittask

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.getViewModelFactory
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays an add or edit task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class AddEditTaskActivity : BaseActivity(), View.OnClickListener {

    private val addEditTaskViewModel: AddEditTaskViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        val taskId: String? = intent.getStringExtra(TasksActivity.EXTRA_TASK_ID)

        findViewById<View>(R.id.fabAddEditTask).setOnClickListener(this)

        setToolbar(R.id.toolbar) {
            setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onClick(v: View) {
        addEditTaskViewModel.addTask()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}