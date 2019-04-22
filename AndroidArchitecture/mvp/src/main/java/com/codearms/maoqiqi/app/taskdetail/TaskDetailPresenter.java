package com.codearms.maoqiqi.app.taskdetail;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Listens to user actions from the UI ({@link TaskDetailFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private String taskId;
    private TasksRepository tasksRepository;
    private TaskDetailContract.View taskDetailView;

    TaskDetailPresenter(String taskId, TasksRepository tasksRepository, TaskDetailContract.View taskDetailView) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
        this.taskDetailView = taskDetailView;
        this.taskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        getTask();
    }

    @Override
    public void getTask() {
        if (isInvalidTaskId()) return;

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                if (taskDetailView.isActive()) taskDetailView.showTask(taskBean);
            }

            @Override
            public void onDataNotAvailable() {
                if (taskDetailView.isActive()) taskDetailView.showMessage(MessageMap.NO_DATA);
            }
        });
    }

    @Override
    public void completeTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.completeTask(taskId);
        taskDetailView.showMessage(MessageMap.COMPLETE);
    }

    @Override
    public void activateTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.activateTask(taskId);
        taskDetailView.showMessage(MessageMap.ACTIVE);
    }

    @Override
    public void deleteTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.deleteTask(taskId);
        taskDetailView.deleteTask();
    }

    private boolean isInvalidTaskId() {
        if (taskId == null || taskId.equals("")) {
            taskDetailView.showMessage(MessageMap.NO_ID);
            return true;
        }
        return false;
    }
}