package com.codearms.maoqiqi.app.taskdetail

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class TaskDetailFragment : BaseFragment(), TaskDetailContract.View {

    override lateinit var presenter: TaskDetailContract.Presenter
    override val isActive: Boolean
        get() = isAdded

    private lateinit var cbComplete: CheckBox
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_task_detail, container, false)
        with(root) {
            cbComplete = findViewById(R.id.cbComplete)
            tvTitle = findViewById(R.id.tvTitle)
            tvDescription = findViewById(R.id.tvDescription)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                presenter.deleteTask()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the task was edited successfully, go back to the list.
        if (requestCode == TasksActivity.REQUEST_CODE && resultCode == TasksActivity.RESULT_ADD_TASK) {
            activity?.apply {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(TasksActivity.RESULT_EDIT_TASK)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showTask(taskBean: TaskBean) {
        tvTitle.text = taskBean.title
        tvDescription.text = taskBean.description

        with(cbComplete) {
            isChecked = taskBean.isCompleted
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    presenter.completeTask()
                } else {
                    presenter.activateTask()
                }
            }
        }
    }

    override fun deleteTask() {
        activity?.apply {
            // If the task was deleted successfully, go back to the list.
            setResult(TasksActivity.RESULT_DELETE_TASK)
            finish()
        }
    }

    override fun editTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    override fun showMessage(message: String) {
        view?.show(MessageMap.get(message))
    }

    companion object {
        fun newInstance(): TaskDetailFragment = TaskDetailFragment()
    }
}