package com.codearms.maoqiqi.app.addedittask

import android.os.Bundle
import android.view.View
import com.codearms.maoqiqi.app.Injection
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.add
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays an add or edit task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class AddEditTaskActivity : BaseActivity(), View.OnClickListener {

    private lateinit var addEditTaskFragment: AddEditTaskFragment
    private lateinit var presenter: AddEditTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        val taskId = intent.getStringExtra(TasksActivity.EXTRA_TASK_ID)

        findViewById<View>(R.id.fabAddEditTask).setOnClickListener(this)

        setToolbar(R.id.toolbar) {
            setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
            setDisplayHomeAsUpEnabled(true)
        }

        addEditTaskFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?
                ?: AddEditTaskFragment.newInstance().also { add(R.id.contentFrame, it) }

        // Prevent the presenter from loading data from the repository if this is a config change.
        // Data might not have loaded when the config change happen, so we saved the state.
        val shouldLoadDataFromRepo = savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO)
                ?: true

        presenter = AddEditTaskPresenter(taskId, Injection.provideTasksRepository(applicationContext),
                addEditTaskFragment, shouldLoadDataFromRepo)
    }

    override fun onClick(v: View) {
        presenter.addTask(addEditTaskFragment.title, addEditTaskFragment.description)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state so that next time we know if we need to refresh data.
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO, presenter.isDataMissing)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val SHOULD_LOAD_DATA_FROM_REPO = "SHOULD_LOAD_DATA_FROM_REPO"
    }
}