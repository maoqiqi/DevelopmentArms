package com.codearms.maoqiqi.app.taskdetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Exposes the data to be used in the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 17:28
 */
public class TaskDetailViewModel extends ViewModel {

    private String taskId;
    private TasksRepository tasksRepository;

    public final MutableLiveData<String> observableTitle = new MutableLiveData<>();
    public final MutableLiveData<String> observableDescription = new MutableLiveData<>();
    public final MutableLiveData<Boolean> observableCompleted = new MutableLiveData<>();

    private final MutableLiveData<Event<Object>> taskDetailEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> message = new MutableLiveData<>();

    void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    MutableLiveData<Event<Object>> getTaskDetailEvent() {
        return taskDetailEvent;
    }

    MutableLiveData<Event<String>> getMessage() {
        return message;
    }

    public TaskDetailViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    void start() {
        getTask();
    }

    private void getTask() {
        if (isInvalidTaskId()) return;

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                observableTitle.setValue(taskBean.getTitle());
                observableDescription.setValue(taskBean.getDescription());
                observableCompleted.setValue(taskBean.isCompleted());
            }

            @Override
            public void onDataNotAvailable() {
                message.setValue(new Event<>(MessageMap.NO_DATA));
            }
        });
    }

    public void completeTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.completeTask(taskId);
        observableCompleted.setValue(true);
        message.setValue(new Event<>(MessageMap.COMPLETE));
    }

    public void activateTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.activateTask(taskId);
        observableCompleted.setValue(false);
        message.setValue(new Event<>(MessageMap.ACTIVE));
    }

    void deleteTask() {
        if (isInvalidTaskId()) return;
        tasksRepository.deleteTask(taskId);
        taskDetailEvent.setValue(new Event<>(new Object()));
    }

    private boolean isInvalidTaskId() {
        if (taskId == null || taskId.equals("")) {
            message.setValue(new Event<>(MessageMap.NO_ID));
            return true;
        }
        return false;
    }
}