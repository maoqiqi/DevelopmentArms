package com.codearms.maoqiqi.app.addedittask;

import androidx.annotation.NonNull;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
    private BaseSchedulerProvider schedulerProvider;

    @NonNull
    private CompositeDisposable compositeDisposable;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId          ID of the task to edit or null for a new task
     * @param tasksRepository a repository of data for tasks
     * @param addEditTaskView the add/edit view
     * @param isDataMissing   whether data needs to be loaded or not (for config changes)
     */
    AddEditTaskPresenter(String taskId, TasksRepository tasksRepository, AddEditTaskContract.View addEditTaskView, boolean isDataMissing, BaseSchedulerProvider schedulerProvider) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
        this.addEditTaskView = addEditTaskView;
        this.isDataMissing = isDataMissing;
        this.addEditTaskView.setPresenter(this);
        this.schedulerProvider = schedulerProvider;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        if (!isNewTask() && isDataMissing) getTask();
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void getTask() {
        if (isNewTask()) throw new RuntimeException("getTask() was called but task is new.");

        Disposable disposable = tasksRepository.getTask(taskId)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(new Consumer<TaskBean>() {
                    @Override
                    public void accept(TaskBean taskBean) {
                        addEditTaskView.setData(taskBean);
                        isDataMissing = false;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        addEditTaskView.showMessage(MessageMap.NO_DATA);
                    }
                });
        compositeDisposable.add(disposable);
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