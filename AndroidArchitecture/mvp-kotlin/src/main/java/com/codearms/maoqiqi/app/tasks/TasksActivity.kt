package com.codearms.maoqiqi.app.tasks

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.codearms.maoqiqi.app.Injection
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.utils.add
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays task list screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class TasksActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tasksFragment: TasksFragment
    private lateinit var tasksPresenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        // Set up the toolbar.
        setToolbar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_home)
            setDisplayHomeAsUpEnabled(true)
        }

        // Set up floating action button
        findViewById<View>(R.id.fabAddTask).setOnClickListener(this)

        tasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment?
                ?: TasksFragment.newInstance().also { add(R.id.contentFrame, it) }

        // Create the presenter
        tasksPresenter = TasksPresenter(Injection.provideTasksRepository(applicationContext), tasksFragment)

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
            tasksPresenter.filtering = currentFiltering
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, getString(R.string.app_id, packageName), Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        tasksFragment.addTask()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_FILTERING_KEY, tasksPresenter.filtering)
        super.onSaveInstanceState(outState)
    }

    companion object {

        const val REQUEST_CODE = 1
        const val RESULT_ADD_TASK = 2
        const val RESULT_EDIT_TASK = 3
        const val RESULT_DELETE_TASK = 4

        const val EXTRA_TASK_ID = "TASK_ID"

        private const val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"
    }
}