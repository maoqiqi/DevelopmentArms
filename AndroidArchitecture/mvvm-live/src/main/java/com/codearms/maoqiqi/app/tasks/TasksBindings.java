package com.codearms.maoqiqi.app.tasks;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link TaskBean} list.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 12:12
 */
public class TasksBindings {

    @BindingAdapter("android:onRefresh")
    public static void setRefresh(SwipeRefreshLayout swipeRefreshLayout, final TasksViewModel tasksViewModel) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tasksViewModel.loadTasks(false);
            }
        });
    }

    @BindingAdapter("app:items")
    public static void setItems(RecyclerView recyclerView, List<TaskBean> taskBeanList) {
        TasksFragment.TasksAdapter adapter = (TasksFragment.TasksAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setData(taskBeanList);
        }
    }
}