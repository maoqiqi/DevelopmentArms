package com.codearms.maoqiqi.app.addedittask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codearms.maoqiqi.app.Event;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Exposes the data to be used in the add task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 18:18
 */
public class AddEditTaskViewModel extends ViewModel {

    private String taskId;
    private TasksRepository tasksRepository;

    // Two-way databinding, exposing MutableLiveData
    public final MutableLiveData<String> observableTitle = new MutableLiveData<>();
    // Two-way databinding, exposing MutableLiveData
    public final MutableLiveData<String> observableDescription = new MutableLiveData<>();

    boolean isDataMissing = true;

    private final MutableLiveData<Event<Object>> addTaskEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> message = new MutableLiveData<>();

    void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    MutableLiveData<Event<Object>> getAddTaskEvent() {
        return addTaskEvent;
    }

    MutableLiveData<Event<String>> getMessage() {
        return message;
    }

    public AddEditTaskViewModel(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    void start() {
        if (!isNewTask() && isDataMissing) getTask();
    }

    private void getTask() {
        if (isNewTask()) throw new RuntimeException("getTask() was called but task is new.");

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                observableTitle.setValue(taskBean.getTitle());
                observableDescription.setValue(taskBean.getDescription());
                isDataMissing = false;
            }

            @Override
            public void onDataNotAvailable() {
                message.setValue(new Event<>(MessageMap.NO_DATA));
            }
        });
    }

    // Called when clicking on fab.
    void addTask() {
        if (observableTitle.getValue() == null || "".equals(observableTitle.getValue()) ||
                observableDescription.getValue() == null || "".equals(observableDescription.getValue())) {
            message.setValue(new Event<>(MessageMap.ENTER));
            return;
        }

        TaskBean taskBean;
        if (isNewTask()) {
            taskBean = new TaskBean(observableTitle.getValue(), observableDescription.getValue(), false);
            tasksRepository.addTask(taskBean);
        } else {
            taskBean = new TaskBean(taskId, observableTitle.getValue(), observableDescription.getValue(), false);
            tasksRepository.updateTask(taskBean);
        }

        // After an add or edit, go back to the list.
        addTaskEvent.setValue(new Event<>(new Object()));
    }

    private boolean isNewTask() {
        return taskId == null;
    }
}