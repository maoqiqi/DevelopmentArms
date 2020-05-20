package com.codearms.maoqiqi.app.addedittask;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.ViewModelFactory;
import com.codearms.maoqiqi.app.base.BaseActivity;
import com.codearms.maoqiqi.app.tasks.TasksActivity;

/**
 * Displays an add or edit task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class AddEditTaskActivity extends BaseActivity implements View.OnClickListener {

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

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        addEditTaskViewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);
        addEditTaskViewModel.setTaskId(taskId);

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
}