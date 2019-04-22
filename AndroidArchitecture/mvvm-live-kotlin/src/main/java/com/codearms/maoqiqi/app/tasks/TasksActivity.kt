package com.codearms.maoqiqi.app.tasks

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.utils.add
import com.codearms.maoqiqi.app.utils.obtainViewModel
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays task list screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class TasksActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tasksFragment: TasksFragment

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

        // Use a Factory to inject dependencies into the ViewModel
        val tasksViewModel = obtainViewModel(TasksViewModel::class.java)

        // Link View and ViewModel
        tasksFragment.setViewModel(tasksViewModel)
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

    companion object {

        const val REQUEST_CODE = 1
        const val RESULT_ADD_TASK = 2
        const val RESULT_EDIT_TASK = 3
        const val RESULT_DELETE_TASK = 4

        const val EXTRA_TASK_ID = "TASK_ID"
    }
}