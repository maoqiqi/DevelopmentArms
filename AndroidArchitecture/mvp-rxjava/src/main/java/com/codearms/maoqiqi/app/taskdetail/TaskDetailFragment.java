package com.codearms.maoqiqi.app.taskdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.addedittask.AddEditTaskActivity;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.tasks.TasksActivity;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.SnackbarUtils;

/**
 * Main UI for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
public class TaskDetailFragment extends BaseFragment implements TaskDetailContract.View {

    private TaskDetailContract.Presenter presenter;

    private CheckBox cbComplete;
    private TextView tvTitle;
    private TextView tvDescription;

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_task_detail, container, false);

        cbComplete = root.findViewById(R.id.cbComplete);
        tvTitle = root.findViewById(R.id.tvTitle);
        tvDescription = root.findViewById(R.id.tvDescription);

        setHasOptionsMenu(true);

        return root;
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
                presenter.deleteTask();
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
        presenter.start();
    }

    @Override
    public void showTask(TaskBean taskBean) {
        tvTitle.setText(taskBean.getTitle());
        tvDescription.setText(taskBean.getDescription());

        cbComplete.setChecked(taskBean.isCompleted());
        cbComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    presenter.completeTask();
                } else {
                    presenter.activateTask();
                }
            }
        });
    }

    @Override
    public void deleteTask() {
        if (getActivity() == null) return;
        // If the task was deleted successfully, go back to the list.
        getActivity().setResult(TasksActivity.RESULT_DELETE_TASK);
        getActivity().finish();
    }

    @Override
    public void editTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, TasksActivity.REQUEST_CODE);
    }

    @Override
    public void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }
}