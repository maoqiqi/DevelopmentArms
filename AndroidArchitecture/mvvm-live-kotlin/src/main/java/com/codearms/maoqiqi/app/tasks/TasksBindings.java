package com.codearms.maoqiqi.app.tasks;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link TaskBean} list.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 12:12
 */
public class TasksBindings {

    @BindingAdapter("onRefresh")
    public static void setRefresh(SwipeRefreshLayout swipeRefreshLayout, final TasksViewModel tasksViewModel) {
        swipeRefreshLayout.setOnRefreshListener(() -> tasksViewModel.loadTasks(false));
    }

    @BindingAdapter("items")
    public static void setItems(RecyclerView recyclerView, List<TaskBean> taskBeanList) {
        TasksFragment.TasksAdapter adapter = (TasksFragment.TasksAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setData(taskBeanList);
        }
    }
}