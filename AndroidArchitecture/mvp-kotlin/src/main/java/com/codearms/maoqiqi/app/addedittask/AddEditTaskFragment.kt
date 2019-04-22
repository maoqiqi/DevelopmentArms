package com.codearms.maoqiqi.app.addedittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class AddEditTaskFragment : BaseFragment(), AddEditTaskContract.View {

    override lateinit var presenter: AddEditTaskContract.Presenter
    override val isActive: Boolean
        get() = isAdded

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_add_edit_task, container, false)
        with(root) {
            editTextTitle = findViewById(R.id.editTextTitle)
            editTextDescription = findViewById(R.id.editTextDescription)
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setData(taskBean: TaskBean) {
        editTextTitle.setText(taskBean.title)
        editTextDescription.setText(taskBean.description)
    }

    override fun showTasks() {
        activity?.apply {
            setResult(TasksActivity.RESULT_ADD_TASK)
            finish()
        }
    }

    override fun showMessage(message: String) {
        view?.show(MessageMap.get(message))
    }

    override fun getTitle(): String {
        return editTextTitle.text.toString()
    }

    override fun getDescription(): String {
        return editTextDescription.text.toString()
    }

    companion object {
        fun newInstance(): AddEditTaskFragment = AddEditTaskFragment()
    }
}