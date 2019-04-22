package com.codearms.maoqiqi.app.addedittask;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    private String taskId;
    private TasksRepository tasksRepository;
    private AddEditTaskContract.View addEditTaskView;
    private boolean isDataMissing;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId          ID of the task to edit or null for a new task
     * @param tasksRepository a repository of data for tasks
     * @param addEditTaskView the add/edit view
     * @param isDataMissing   whether data needs to be loaded or not (for config changes)
     */
    AddEditTaskPresenter(String taskId, TasksRepository tasksRepository, AddEditTaskContract.View addEditTaskView, boolean isDataMissing) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
        this.addEditTaskView = addEditTaskView;
        this.isDataMissing = isDataMissing;
        this.addEditTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask() && isDataMissing) getTask();
    }

    @Override
    public void getTask() {
        if (isNewTask()) throw new RuntimeException("getTask() was called but task is new.");

        tasksRepository.getTask(taskId, new TasksDataSource.GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                if (addEditTaskView.isActive()) addEditTaskView.setData(taskBean);
                isDataMissing = false;
            }

            @Override
            public void onDataNotAvailable() {
                if (addEditTaskView.isActive()) addEditTaskView.showMessage(MessageMap.NO_DATA);
            }
        });
    }

    @Override
    public boolean isDataMissing() {
        return isDataMissing;
    }

    // Called when clicking on fab.
    @Override
    public void addTask(String title, String description) {
        if (title.equals("") || description.equals("")) {
            addEditTaskView.showMessage(MessageMap.ENTER);
            return;
        }

        TaskBean taskBean;
        if (isNewTask()) {
            taskBean = new TaskBean(title, description, false);
            tasksRepository.addTask(taskBean);
        } else {
            taskBean = new TaskBean(taskId, title, description, false);
            tasksRepository.updateTask(taskBean);
        }
        // After an add or edit, go back to the list.
        addEditTaskView.showTasks();
    }

    private boolean isNewTask() {
        return taskId == null;
    }
}