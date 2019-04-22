package com.codearms.maoqiqi.app.tasks

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.PopupMenu
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.databinding.FragmentTasksBinding
import com.codearms.maoqiqi.app.databinding.ItemTaskBinding
import com.codearms.maoqiqi.app.statistics.StatisticsActivity
import com.codearms.maoqiqi.app.taskdetail.TaskDetailActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the task list screen. User can choose to view all, active or completed tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class TasksFragment : BaseFragment() {

    private lateinit var tasksViewModel: TasksViewModel

    fun setViewModel(tasksViewModel: TasksViewModel) {
        this.tasksViewModel = tasksViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = DataBindingUtil.inflate<FragmentTasksBinding>(inflater, R.layout.fragment_tasks, container, false)
        binding.tasksViewModel = tasksViewModel
        binding.setLifecycleOwner(activity)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = TasksAdapter(context!!, null, tasksViewModel, activity!!)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Subscribe to "open task" event
        tasksViewModel.taskItemEvent.observe(this, Observer { stringEvent ->
            val taskId = stringEvent!!.getContentIfNotHandled()
            if (taskId != null) onOpenTaskDetails(taskId)
        })
        tasksViewModel.message.observe(this, Observer { stringEvent ->
            val message = stringEvent!!.getContentIfNotHandled()
            if (message != null) showMessage(message)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item!!.itemId) {
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_clear -> tasksViewModel.clearCompletedTasks()
            R.id.menu_refresh -> tasksViewModel.loadTasks(true)
            R.id.menu_statistics -> startActivity(Intent(context, StatisticsActivity::class.java))
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        tasksViewModel.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        tasksViewModel.result(requestCode, resultCode)
    }

    private fun showFilteringPopUpMenu() {
        if (activity == null) return

        val popup = PopupMenu(activity, activity!!.findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.menu_filter_tasks, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_active -> tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
                R.id.menu_completed -> tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
                else -> tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
            }
            tasksViewModel.loadTasks(false)
            true
        }

        popup.show()
    }

    private fun onOpenTaskDetails(taskId: String?) {
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    internal fun addTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    fun showMessage(message: String?) {
        view?.show(MessageMap.get(message))
    }

    internal class TasksAdapter(
            private val context: Context,
            private var taskBeanList: List<TaskBean>?,
            private val tasksViewModel: TasksViewModel,
            private val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<ItemTaskBinding>(LayoutInflater.from(context), R.layout.item_task, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val taskBean = taskBeanList!![position]
            holder.binding.taskBean = taskBean
            holder.binding.tasksViewModel = tasksViewModel
            holder.binding.setLifecycleOwner(lifecycleOwner)
            holder.binding.executePendingBindings()
        }

        override fun getItemCount(): Int = taskBeanList?.size ?: 0

        fun setData(taskBeanList: List<TaskBean>?) {
            this.taskBeanList = taskBeanList
            notifyDataSetChanged()
        }
    }

    internal class ViewHolder internal constructor(internal var binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        fun newInstance(): TasksFragment = TasksFragment()
    }
}