package com.codearms.maoqiqi.app.tasks;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.databinding.FragmentTasksBinding;
import com.codearms.maoqiqi.app.databinding.ItemTaskBinding;
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
public class TasksFragment extends BaseFragment {

    private TasksViewModel tasksViewModel;

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    public void setViewModel(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentTasksBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks, container, false);
        binding.setTasksViewModel(tasksViewModel);
        binding.setLifecycleOwner(getActivity());

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new TasksAdapter(getContext(), null, tasksViewModel, getActivity()));

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Subscribe to "open task" event
        tasksViewModel.getTaskItemEvent().observe(this, new Observer<Event<String>>() {
            @Override
            public void onChanged(Event<String> stringEvent) {
                String taskId = stringEvent.getContentIfNotHandled();
                if (taskId != null) onOpenTaskDetails(taskId);
            }
        });
        tasksViewModel.getMessage().observe(this, new Observer<Event<String>>() {
            @Override
            public void onChanged(Event<String> stringEvent) {
                String message = stringEvent.getContentIfNotHandled();
                if (message != null) showMessage(message);
            }
        });
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
                tasksViewModel.clearCompletedTasks();
                break;
            case R.id.menu_refresh:
                tasksViewModel.loadTasks(true);
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
        tasksViewModel.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tasksViewModel.result(requestCode, resultCode);
    }

    private void showFilteringPopUpMenu() {
        if (getActivity() == null) return;

        PopupMenu popup = new PopupMenu(getActivity(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.menu_filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_active:
                        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.menu_completed:
                        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                tasksViewModel.loadTasks(false);
                return true;
            }
        });

        popup.show();
    }

    private void onOpenTaskDetails(String taskId) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    protected void addTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    private void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }

    protected static final class TasksAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private List<TaskBean> taskBeanList;
        private TasksViewModel tasksViewModel;
        private LifecycleOwner lifecycleOwner;

        private TasksAdapter(Context context, List<TaskBean> taskBeanList, TasksViewModel tasksViewModel, LifecycleOwner lifecycleOwner) {
            this.context = context;
            this.taskBeanList = taskBeanList;
            this.tasksViewModel = tasksViewModel;
            this.lifecycleOwner = lifecycleOwner;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            ItemTaskBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_task, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TaskBean taskBean = taskBeanList.get(position);
            holder.binding.setTaskBean(taskBean);
            holder.binding.setTasksViewModel(tasksViewModel);
            holder.binding.setLifecycleOwner(lifecycleOwner);
            holder.binding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return taskBeanList == null ? 0 : taskBeanList.size();
        }

        protected void setData(List<TaskBean> taskBeanList) {
            this.taskBeanList = taskBeanList;
            notifyDataSetChanged();
        }
    }

    private static final class ViewHolder extends RecyclerView.ViewHolder {

        ItemTaskBinding binding;

        ViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}