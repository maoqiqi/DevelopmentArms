package com.codearms.maoqiqi.app.addedittask;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.codearms.maoqiqi.app.CallBack;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Exposes the data to be used in the add task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 18:18
 */
public class AddEditTaskViewModel extends BaseObservable {

    private String taskId;
    private TasksRepository tasksRepository;

    // Two-way databinding, exposing MutableLiveData
    public final ObservableField<String> observableTitle = new ObservableField<>();
    // Two-way databinding, exposing MutableLiveData
    public final ObservableField<String> observableDescription = new ObservableField<>();

    boolean isDataMissing = true;

    private AddEditTaskListener addEditTaskListener;
    private CallBack callBack;

    void setAddEditTaskListener(AddEditTaskListener addEditTaskListener) {
        this.addEditTaskListener = addEditTaskListener;
    }

    void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    AddEditTaskViewModel(String taskId, TasksRepository tasksRepository) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
    }

    void destroy() {
        addEditTaskListener = null;
        callBack = null;
    }

    void start() {
        if (!isNewTask() && isDataMissing) getTask();
    }

    private void getTask() {
        if (isNewTask()) throw new RuntimeException("getTask() was called but task is new.");

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                observableTitle.set(taskBean.getTitle());
                observableDescription.set(taskBean.getDescription());
                isDataMissing = false;
            }

            @Override
            public void onDataNotAvailable() {
                if (callBack != null) callBack.showMessage(MessageMap.NO_DATA);
            }
        });
    }

    // Called when clicking on fab.
    void addTask() {
        if (observableTitle.get() == null || "".equals(observableTitle.get()) ||
                observableDescription.get() == null || "".equals(observableDescription.get())) {
            if (callBack != null) callBack.showMessage(MessageMap.ENTER);
            return;
        }

        TaskBean taskBean;
        if (isNewTask()) {
            taskBean = new TaskBean(observableTitle.get(), observableDescription.get(), false);
            tasksRepository.addTask(taskBean);
        } else {
            taskBean = new TaskBean(taskId, observableTitle.get(), observableDescription.get(), false);
            tasksRepository.updateTask(taskBean);
        }

        // After an add or edit, go back to the list.
        if (addEditTaskListener != null) addEditTaskListener.showTasks();
    }

    private boolean isNewTask() {
        return taskId == null;
    }
}