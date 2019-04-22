package com.codearms.maoqiqi.app.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.R;
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

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    private TasksFragment tasksFragment;
    private TasksPresenter tasksPresenter;

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

        // Create the presenter
        tasksPresenter = new TasksPresenter(Injection.provideTasksRepository(getApplicationContext()), tasksFragment);

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TasksFilterType currentFiltering = (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            tasksPresenter.setFiltering(currentFiltering);
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, tasksPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }
}