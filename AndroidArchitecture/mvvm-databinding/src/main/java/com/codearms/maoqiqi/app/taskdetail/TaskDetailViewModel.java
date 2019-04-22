package com.codearms.maoqiqi.app.taskdetail;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.codearms.maoqiqi.app.CallBack;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Exposes the data to be used in the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 17:28
 */
public class TaskDetailViewModel extends BaseObservable {

    private String taskId;
    private TasksRepository tasksRepository;

    public final ObservableField<String> observableTitle = new ObservableField<>();
    public final ObservableField<String> observableDescription = new ObservableField<>();
    public final ObservableBoolean observableCompleted = new ObservableBoolean();

    private TaskDetailListener taskDetailListener;
    private CallBack callBack;

    void setTaskDetailListener(TaskDetailListener taskDetailListener) {
        this.taskDetailListener = taskDetailListener;
    }

    void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    TaskDetailViewModel(String taskId, TasksRepository tasksRepository) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
    }

    void destroy() {
        taskDetailListener = null;
        callBack = null;
    }

    void start() {
        getTask();
    }

    private void getTask() {
        if (isInvalidTaskId()) return;

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                observableTitle.set(taskBean.getTitle());
                observableDescription.set(taskBean.getDescription());
                observableCompleted.set(taskBean.isCompleted());
            }

            @Override
            public void onDataNotAvailable() {
                if (callBack != null) callBack.showMessage(MessageMap.NO_DATA);
            }
        });
    }

    public void completeTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.completeTask(taskId);
        observableCompleted.set(true);
        if (callBack != null) callBack.showMessage(MessageMap.COMPLETE);
    }

    public void activateTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.activateTask(taskId);
        observableCompleted.set(false);
        if (callBack != null) callBack.showMessage(MessageMap.ACTIVE);
    }

    void deleteTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.deleteTask(taskId);
        if (taskDetailListener != null) taskDetailListener.deleteTask();
    }

    private boolean isInvalidTaskId() {
        if (taskId == null || taskId.equals("")) {
            if (callBack != null) callBack.showMessage(MessageMap.NO_ID);
            return true;
        }
        return false;
    }
}