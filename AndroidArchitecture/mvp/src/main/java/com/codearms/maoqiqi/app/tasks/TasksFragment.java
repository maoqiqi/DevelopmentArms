package com.codearms.maoqiqi.app.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.statistics.StatisticsActivity;
import com.codearms.maoqiqi.app.taskdetail.TaskDetailActivity;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.SnackbarUtils;

import java.util.List;

/**
 * Main UI for the task list screen. User can choose to view all, active or completed tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
public class TasksFragment extends BaseFragment implements TasksContract.View {

    private TasksContract.Presenter presenter;

    private TasksAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvFilteringLabel;
    private RecyclerView recyclerView;
    private TextView tvNoTasks;

    @Override
    public void setPresenter(TasksContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TasksAdapter(getContext(), null, taskItemListener);
    }

    /**
     * Listener for clicks on tasks in the RecyclerView.
     */
    private TaskItemListener taskItemListener = new TaskItemListener() {

        @Override
        public void onActivateTask(TaskBean activateTaskBean) {
            presenter.activateTask(activateTaskBean);
        }

        @Override
        public void onCompleteTask(TaskBean completedTaskBean) {
            presenter.completeTask(completedTaskBean);
        }

        @Override
        public void onOpenTaskDetails(TaskBean requestedTaskBean) {
            openTaskDetails(requestedTaskBean.getId());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Set up progress indicator
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadTasks(false);
            }
        });

        // Set up tasks view
        tvFilteringLabel = root.findViewById(R.id.tvFilteringLabel);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        // Set up no tasks view
        tvNoTasks = root.findViewById(R.id.tvNoTasks);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_clear:
                presenter.clearCompletedTasks();
                break;
            case R.id.menu_refresh:
                presenter.loadTasks(true);
                break;
            case R.id.menu_statistics:
                startActivity(new Intent(getContext(), StatisticsActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.result(requestCode, resultCode);
    }

    @Override
    public void setLoadingIndicator(final boolean showLoading) {
        // Make sure setRefreshing() is called after the layout is done with everything else.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(showLoading);
            }
        });
    }

    @Override
    public void showFilterLabel() {
        switch (presenter.getFiltering()) {
            case ALL_TASKS:
                tvFilteringLabel.setText(R.string.all_tasks);
                break;
            case ACTIVE_TASKS:
                tvFilteringLabel.setText(R.string.active_tasks);
                break;
            case COMPLETED_TASKS:
                tvFilteringLabel.setText(R.string.completed_tasks);
                break;
        }
    }

    @Override
    public void showTasks(List<TaskBean> taskBeanList) {
        tvFilteringLabel.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        tvNoTasks.setVisibility(View.GONE);
        adapter.setData(taskBeanList);
    }

    @Override
    public void showNoTasks() {
        switch (presenter.getFiltering()) {
            case ALL_TASKS:
                showNoTasksViews(getString(R.string.no_all_tasks), R.drawable.ic_all_tasks);
                break;
            case ACTIVE_TASKS:
                showNoTasksViews(getString(R.string.no_active_tasks), R.drawable.ic_active_tasks);
                break;
            case COMPLETED_TASKS:
                showNoTasksViews(getString(R.string.no_completed_tasks), R.drawable.ic_completed_tasks);
                break;
        }
    }

    private void showNoTasksViews(String text, int iconRes) {
        tvFilteringLabel.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tvNoTasks.setVisibility(View.VISIBLE);

        tvNoTasks.setText(text);
        tvNoTasks.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
    }

    @Override
    public void showFilteringPopUpMenu() {
        if (getActivity() == null) return;

        PopupMenu popup = new PopupMenu(getActivity(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.menu_filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_active:
                        presenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.menu_completed:
                        presenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        presenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                presenter.loadTasks(false);
                return true;
            }
        });

        popup.show();
    }

    @Override
    public void openTaskDetails(String taskId) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    @Override
    public void addTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    @Override
    public void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }

    private static final class TasksAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private List<TaskBean> taskBeanList;
        private TaskItemListener listener;

        TasksAdapter(Context context, List<TaskBean> taskBeanList, TaskItemListener listener) {
            this.context = context;
            this.taskBeanList = taskBeanList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TaskBean taskBean = taskBeanList.get(position);

            holder.llItem.setBackgroundResource(taskBean.isCompleted() ? R.drawable.bg_item_completed : R.drawable.bg_item_active);
            holder.cbComplete.setChecked(taskBean.isCompleted());
            holder.tvTitle.setText(taskBean.getTitle());

            holder.cbComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskBean.isCompleted()) {
                        listener.onActivateTask(taskBean);
                    } else {
                        listener.onCompleteTask(taskBean);
                    }
                }
            });
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOpenTaskDetails(taskBean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return taskBeanList == null ? 0 : taskBeanList.size();
        }

        void setData(List<TaskBean> taskBeanList) {
            this.taskBeanList = taskBeanList;
            notifyDataSetChanged();
        }
    }

    private static final class ViewHolder extends RecyclerView.ViewHolder {

        View llItem;
        CheckBox cbComplete;
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.llItem);
            cbComplete = itemView.findViewById(R.id.cbComplete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    private interface TaskItemListener {

        void onActivateTask(TaskBean activateTaskBean);

        void onCompleteTask(TaskBean completedTaskBean);

        void onOpenTaskDetails(TaskBean requestedTaskBean);
    }
}