package com.codearms.maoqiqi.app.taskdetail;

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
 * Displays task details screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class TaskDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String TASK_DETAIL_VIEW_MODEL = "TASK_DETAIL_VIEW_MODEL";

    private String taskId;
    private TaskDetailFragment taskDetailFragment;
    private TaskDetailViewModel taskDetailViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Get the requested task id
        taskId = getIntent().getStringExtra(TasksActivity.EXTRA_TASK_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.task_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.fabEditTask).setOnClickListener(this);

        taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, taskDetailFragment).commit();
        }

        @SuppressWarnings("unchecked")
        ViewModelHolder<TaskDetailViewModel> viewModelHolder = (ViewModelHolder<TaskDetailViewModel>) getSupportFragmentManager().findFragmentByTag(TASK_DETAIL_VIEW_MODEL);
        if (viewModelHolder != null && viewModelHolder.getViewModel() != null) {
            taskDetailViewModel = viewModelHolder.getViewModel();
        } else {
            taskDetailViewModel = new TaskDetailViewModel(taskId, Injection.provideTasksRepository(this));
            getSupportFragmentManager().beginTransaction().add(ViewModelHolder.createContainer(taskDetailViewModel), TASK_DETAIL_VIEW_MODEL).commit();
        }
        taskDetailViewModel.setTaskDetailListener(taskDetailFragment);
        taskDetailViewModel.setCallBack(taskDetailFragment);

        taskDetailFragment.setViewModel(taskDetailViewModel);
    }

    @Override
    public void onClick(View v) {
        taskDetailFragment.editTask(taskId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        taskDetailViewModel.destroy();
        super.onDestroy();
    }
}