package com.codearms.maoqiqi.app.addedittask;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.base.BaseActivity;
import com.codearms.maoqiqi.app.tasks.TasksActivity;

/**
 * Displays an add or edit task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class AddEditTaskActivity extends BaseActivity implements View.OnClickListener {

    private static final String SHOULD_LOAD_DATA_FROM_REPO = "SHOULD_LOAD_DATA_FROM_REPO";

    private AddEditTaskFragment addEditTaskFragment;
    private AddEditTaskPresenter presenter;

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

        addEditTaskFragment = (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, addEditTaskFragment).commit();
        }

        boolean shouldLoadDataFromRepo = true;
        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO);
        }

//        presenter = new AddEditTaskPresenter(taskId, Injection.provideTasksRepository(getApplicationContext()),
//                addEditTaskFragment, shouldLoadDataFromRepo, Injection.provideSchedulerProvider());
    }

    @Override
    public void onClick(View v) {
        presenter.addTask(addEditTaskFragment.getTitle(), addEditTaskFragment.getDescription());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the state so that next time we know if we need to refresh data.
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO, presenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}