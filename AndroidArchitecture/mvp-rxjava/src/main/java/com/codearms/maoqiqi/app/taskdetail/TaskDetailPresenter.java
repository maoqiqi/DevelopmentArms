package com.codearms.maoqiqi.app.taskdetail;

import android.support.annotation.NonNull;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Listens to user actions from the UI ({@link TaskDetailFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private String taskId;
    private TasksRepository tasksRepository;
    private TaskDetailContract.View taskDetailView;
    private BaseSchedulerProvider schedulerProvider;

    @NonNull
    private CompositeDisposable compositeDisposable;

    TaskDetailPresenter(String taskId, TasksRepository tasksRepository, TaskDetailContract.View taskDetailView, BaseSchedulerProvider schedulerProvider) {
        this.taskId = taskId;
        this.tasksRepository = tasksRepository;
        this.taskDetailView = taskDetailView;
        this.taskDetailView.setPresenter(this);
        this.schedulerProvider = schedulerProvider;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        getTask();
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void getTask() {
        if (isInvalidTaskId()) return;

        Disposable disposable = tasksRepository.getTask(taskId)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(new Consumer<TaskBean>() {
                    @Override
                    public void accept(TaskBean taskBean) {
                        taskDetailView.showTask(taskBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        taskDetailView.showMessage(MessageMap.NO_DATA);
                    }
                });
        compositeDisposable.add(disposable);
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