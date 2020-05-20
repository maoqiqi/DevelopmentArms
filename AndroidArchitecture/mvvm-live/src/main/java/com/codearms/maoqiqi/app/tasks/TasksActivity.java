package com.codearms.maoqiqi.app.tasks;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.ViewModelFactory;
import com.codearms.maoqiqi.app.base.BaseActivity;

/**
 * Displays task list screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class TasksActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_ADD_TASK = 2;
    public static final int RESULT_EDIT_TASK = 3;
    public static final int RESULT_DELETE_TASK = 4;

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private TasksFragment tasksFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set up floating action button
        findViewById(R.id.fabAddTask).setOnClickListener(this);

        tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, tasksFragment).commit();
        }

        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        TasksViewModel tasksViewModel = ViewModelProviders.of(this, factory).get(TasksViewModel.class);

        // Link View and ViewModel
        tasksFragment.setViewModel(tasksViewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, getString(R.string.app_id, getPackageName()), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        tasksFragment.addTask();
    }
}