package com.codearms.maoqiqi.app.addedittask

import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.databinding.FragmentAddEditTaskBinding
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.getViewModelFactory
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class AddEditTaskFragment : BaseFragment() {

    private val addEditTaskViewModel by viewModels<AddEditTaskViewModel> { getViewModelFactory() }
    // add_edit_task_fragment
//    private val args:AddEditTaskFragment by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = DataBindingUtil.inflate<FragmentAddEditTaskBinding>(inflater, R.layout.fragment_add_edit_task, container, false)
        binding.addEditTaskViewModel = addEditTaskViewModel
        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addEditTaskViewModel.addTaskEvent.observe(viewLifecycleOwner, Observer { objectEvent ->
            if (objectEvent!!.getContentIfNotHandled() != null) {
                showTasks()
            }
        })
        addEditTaskViewModel.message.observe(viewLifecycleOwner, Observer { stringEvent ->
            val message = stringEvent!!.getContentIfNotHandled()
            if (message != null) showMessage(message)
        })
    }

    override fun onResume() {
        super.onResume()
        addEditTaskViewModel.start("")
    }

    private fun showTasks() {
        activity?.apply {
            setResult(TasksActivity.RESULT_ADD_TASK)
            finish()
        }
    }

    private fun showMessage(message: String?) {
        view?.show(MessageMap.get(message))
    }
}