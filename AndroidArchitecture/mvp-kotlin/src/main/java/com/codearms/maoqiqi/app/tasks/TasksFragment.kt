package com.codearms.maoqiqi.app.tasks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.statistics.StatisticsActivity
import com.codearms.maoqiqi.app.taskdetail.TaskDetailActivity
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the task list screen. User can choose to view all, active or completed tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class TasksFragment : BaseFragment(), TasksContract.View {

    override lateinit var presenter: TasksContract.Presenter
    override val isActive: Boolean
        get() = isAdded

    private lateinit var tasksAdapter: TasksAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvFilteringLabel: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoTasks: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasksAdapter = TasksAdapter(context!!, null, taskItemListener)
    }

    /**
     * Listener for clicks on tasks in the RecyclerView.
     */
    private val taskItemListener: TaskItemListener = object : TaskItemListener {

        override fun onActivateTask(activateTaskBean: TaskBean) {
            presenter.activateTask(activateTaskBean)
        }

        override fun onCompleteTask(completedTaskBean: TaskBean) {
            presenter.completeTask(completedTaskBean)
        }

        override fun onOpenTaskDetails(requestedTaskBean: TaskBean) {
            openTaskDetails(requestedTaskBean.id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_tasks, container, false)


        with(root) {
            // Set up progress indicator
            swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout).apply {
                setColorSchemeResources(R.color.colorPrimary)
                setOnRefreshListener { presenter.loadTasks(false) }
            }

            // Set up tasks view
            tvFilteringLabel = findViewById(R.id.tvFilteringLabel)
            recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(false)
                itemAnimator = DefaultItemAnimator()
                isNestedScrollingEnabled = false
                adapter = tasksAdapter
            }

            // Set up no tasks view
            tvNoTasks = findViewById(R.id.tvNoTasks)
        }

        setHasOptionsMenu(true)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_clear -> presenter.clearCompletedTasks()
            R.id.menu_refresh -> presenter.loadTasks(true)
            R.id.menu_statistics -> startActivity(Intent(context, StatisticsActivity::class.java))
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.result(requestCode, resultCode)
    }

    override fun setLoadingIndicator(showLoading: Boolean) {
        // Make sure setRefreshing() is called after the layout is done with everything else.
        swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = showLoading }
    }

    override fun showFilterLabel() {
        when (presenter.filtering) {
            TasksFilterType.ALL_TASKS -> tvFilteringLabel.setText(R.string.all_tasks)
            TasksFilterType.ACTIVE_TASKS -> tvFilteringLabel.setText(R.string.active_tasks)
            TasksFilterType.COMPLETED_TASKS -> tvFilteringLabel.setText(R.string.completed_tasks)
        }
    }

    override fun showTasks(taskBeanList: List<TaskBean>) {
        tvFilteringLabel.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        tvNoTasks.visibility = View.GONE
        tasksAdapter.setData(taskBeanList)
    }

    override fun showNoTasks() {
        when (presenter.filtering) {
            TasksFilterType.ALL_TASKS -> showNoTasksViews(getString(R.string.no_all_tasks), R.drawable.ic_all_tasks)
            TasksFilterType.ACTIVE_TASKS -> showNoTasksViews(getString(R.string.no_active_tasks), R.drawable.ic_active_tasks)
            TasksFilterType.COMPLETED_TASKS -> showNoTasksViews(getString(R.string.no_completed_tasks), R.drawable.ic_completed_tasks)
        }
    }

    private fun showNoTasksViews(text: String, iconRes: Int) {
        tvFilteringLabel.visibility = View.GONE
        recyclerView.visibility = View.GONE
        tvNoTasks.visibility = View.VISIBLE

        tvNoTasks.text = text
        tvNoTasks.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
    }

    override fun showFilteringPopUpMenu() {
        val activity = activity ?: return

        PopupMenu(activity, activity.findViewById(R.id.menu_filter)).apply {
            menuInflater.inflate(R.menu.menu_filter_tasks, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_active -> presenter.filtering = TasksFilterType.ACTIVE_TASKS
                    R.id.menu_completed -> presenter.filtering = TasksFilterType.COMPLETED_TASKS
                    else -> presenter.filtering = TasksFilterType.ALL_TASKS
                }
                presenter.loadTasks(false)
                true
            }
            show()
        }
    }

    override fun openTaskDetails(taskId: String) {
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    override fun addTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, TasksActivity.REQUEST_CODE)
    }

    override fun showMessage(message: String) {
        view?.show(MessageMap.get(message))
    }

    private class TasksAdapter internal constructor(
            private val context: Context,
            private var taskBeanList: List<TaskBean>?,
            private val listener: TaskItemListener
    ) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val taskBean = taskBeanList!![position]

            with(holder) {
                with(llItem) {
                    setBackgroundResource(if (taskBean.isCompleted) R.drawable.bg_item_completed else R.drawable.bg_item_active)
                    setOnClickListener { listener.onOpenTaskDetails(taskBean) }
                }
                with(cbComplete) {
                    isChecked = taskBean.isCompleted
                    setOnClickListener {
                        if (taskBean.isCompleted) {
                            listener.onActivateTask(taskBean)
                        } else {
                            listener.onCompleteTask(taskBean)
                        }
                    }
                }
                tvTitle.text = taskBean.title
            }
        }

        override fun getItemCount(): Int = taskBeanList?.size ?: 0

        internal fun setData(taskBeanList: List<TaskBean>) {
            this.taskBeanList = taskBeanList
            notifyDataSetChanged()
        }
    }

    private class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var llItem: View = itemView.findViewById(R.id.llItem)
        internal var cbComplete: CheckBox = itemView.findViewById(R.id.cbComplete)
        internal var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    private interface TaskItemListener {

        fun onActivateTask(activateTaskBean: TaskBean)

        fun onCompleteTask(completedTaskBean: TaskBean)

        fun onOpenTaskDetails(requestedTaskBean: TaskBean)
    }

    companion object {

        fun newInstance(): TasksFragment = TasksFragment()
    }
}