package com.codearms.maoqiqi.app.taskdetail

import androidx.lifecycle.Observer
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.databinding.FragmentTaskDetailBinding
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.getViewModelFactory
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class TaskDetailFragment : BaseFragment() {

    private val taskDetailViewModel: TaskDetailViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = DataBindingUtil.inflate<FragmentTaskDetailBinding>(inflater, R.layout.fragment_task_detail, container, false)
        binding.taskDetailViewModel = taskDetailViewModel
        binding.setLifecycleOwner(activity)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        taskDetailViewModel.taskDetailEvent.observe(viewLifecycleOwner, Observer { objectEvent ->
            if (objectEvent!!.getContentIfNotHandled() != null) {
                deleteTask()
            }
        })
        taskDetailViewModel.message.observe(viewLifecycleOwner, Observer { stringEvent ->
            val message = stringEvent!!.getContentIfNotHandled()
            if (message != null) showMessage(message)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                taskDetailViewModel.deleteTask()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If the task was edited successfully, go back to the list.
        if (requestCode == TasksActivity.REQUEST_CODE && resultCode == TasksActivity.RESULT_ADD_TASK) {
            // If the result comes from the add/edit screen, it's an edit.
            activity?.apply {
                setResult(TasksActivity.RESULT_EDIT_TASK)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        taskDetailViewModel.start()
    }

    fun deleteTask() {
        // If the task was deleted successfully, go back to the list.
        activity?.apply {
            setResult(TasksActivity.RESULT_DELETE_TASK)
            finish()
        }
    }

    fun editTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    private fun showMessage(message: String?) {
        view?.show(MessageMap.get(message))
    }
}