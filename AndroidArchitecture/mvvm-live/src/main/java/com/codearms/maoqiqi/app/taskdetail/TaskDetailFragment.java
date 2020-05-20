package com.codearms.maoqiqi.app.taskdetail;

import androidx.lifecycle.Observer;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.databinding.FragmentTaskDetailBinding;
import com.codearms.maoqiqi.app.tasks.TasksActivity;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.SnackbarUtils;

/**
 * Main UI for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
public class TaskDetailFragment extends BaseFragment {

    private TaskDetailViewModel taskDetailViewModel;

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    public void setViewModel(TaskDetailViewModel taskDetailViewModel) {
        this.taskDetailViewModel = taskDetailViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentTaskDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_detail, container, false);
        binding.setTaskDetailViewModel(taskDetailViewModel);
        binding.setLifecycleOwner(getActivity());
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        taskDetailViewModel.getTaskDetailEvent().observe(this, new Observer<Event<Object>>() {
            @Override
            public void onChanged(Event<Object> objectEvent) {
                if (objectEvent.getContentIfNotHandled() != null) {
                    deleteTask();
                }
            }
        });
        taskDetailViewModel.getMessage().observe(this, new Observer<Event<String>>() {
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
        inflater.inflate(R.menu.menu_task_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                taskDetailViewModel.deleteTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the task was edited successfully, go back to the list.
        if (requestCode == TasksActivity.REQUEST_CODE && resultCode == TasksActivity.RESULT_ADD_TASK) {
            if (getActivity() != null) {
                // If the result comes from the add/edit screen, it's an edit.
                getActivity().setResult(TasksActivity.RESULT_EDIT_TASK);
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        taskDetailViewModel.start();
    }

    private void deleteTask() {
        if (getActivity() == null) return;
        // If the task was deleted successfully, go back to the list.
        getActivity().setResult(TasksActivity.RESULT_DELETE_TASK);
        getActivity().finish();
    }

    protected void editTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    private void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }
}