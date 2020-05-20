package com.codearms.maoqiqi.app.addedittask;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.ViewModelHolder;
import com.codearms.maoqiqi.app.base.BaseActivity;
import com.codearms.maoqiqi.app.tasks.TasksActivity;

/**
 * Displays an add or edit task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class AddEditTaskActivity extends BaseActivity implements View.OnClickListener {

    private static final String ADD_EDIT_VIEW_MODEL = "ADD_EDIT_VIEW_MODEL";

    private AddEditTaskViewModel addEditTaskViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        String taskId = getIntent().getStringExtra(TasksActivity.EXTRA_TASK_ID);

        findViewById(R.id.fabAddEditTask).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(taskId == null ? R.string.add_task : R.string.edit_task);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AddEditTaskFragment addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, addEditTaskFragment).commit();
        }

        @SuppressWarnings("unchecked")
        ViewModelHolder<AddEditTaskViewModel> viewModelHolder = (ViewModelHolder<AddEditTaskViewModel>) getSupportFragmentManager().findFragmentByTag(ADD_EDIT_VIEW_MODEL);
        if (viewModelHolder != null && viewModelHolder.getViewModel() != null) {
            addEditTaskViewModel = viewModelHolder.getViewModel();
        } else {
            addEditTaskViewModel = new AddEditTaskViewModel(taskId, Injection.provideTasksRepository(this));
            getSupportFragmentManager().beginTransaction().add(ViewModelHolder.createContainer(addEditTaskViewModel), ADD_EDIT_VIEW_MODEL).commit();
        }
        addEditTaskViewModel.setAddEditTaskListener(addEditTaskFragment);
        addEditTaskViewModel.setCallBack(addEditTaskFragment);

        addEditTaskFragment.setViewModel(addEditTaskViewModel);
    }

    @Override
    public void onClick(View v) {
        addEditTaskViewModel.addTask();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        addEditTaskViewModel.destroy();
        super.onDestroy();
    }
}