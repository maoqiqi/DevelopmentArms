package com.codearms.maoqiqi.app.data.source.room;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;

/**
 * Use room to create the sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
public class TasksRoomDataSource implements TasksDataSource {

    private static volatile TasksRoomDataSource INSTANCE;

    private final TasksDAO dao;
    private final BaseSchedulerProvider schedulerProvider;

    private TasksRoomDataSource(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        this.dao = TaskDataBase.getInstance(context).tasksDAO();
        this.schedulerProvider = schedulerProvider;
    }

    public static TasksRoomDataSource getInstance(Context context, BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            synchronized (TasksRoomDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRoomDataSource(context, schedulerProvider);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    @Override
    public Single<List<TaskBean>> loadTasks() {
        return dao.loadTasks();
    }

    @Override
    public Single<TaskBean> getTask(final String taskId) {
        return dao.getTaskById(taskId);
    }

    @Override
    public void clearCompletedTasks() {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.deleteCompletedTasks();
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void addTask(final TaskBean taskBean) {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.addTask(taskBean);
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void updateTask(final TaskBean taskBean) {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.updateTask(taskBean);
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        completeTask(completedTaskBean.getId());
    }

    @Override
    public void completeTask(final String taskId) {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.updateCompleted(taskId, true);
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        activateTask(activeTaskBean.getId());
    }

    @Override
    public void activateTask(final String taskId) {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.updateCompleted(taskId, false);
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void deleteTask(final String taskId) {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.deleteTaskById(taskId);
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }

    @Override
    public void deleteAllTasks() {
        Observable.unsafeCreate(new ObservableSource<Boolean>() {
            @Override
            public void subscribe(Observer<? super Boolean> observer) {
                dao.deleteTasks();
                observer.onNext(true);
                observer.onComplete();
            }
        }).subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui()).subscribe();
    }
}