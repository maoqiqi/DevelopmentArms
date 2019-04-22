package com.codearms.maoqiqi.app.data.source.sqlite;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;

import java.util.List;

/**
 * The sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
public class TasksSqliteDataSource implements TasksDataSource {

    private static volatile TasksSqliteDataSource INSTANCE;

    private final TasksDAO dao;

    // Prevent direct instantiation.
    private TasksSqliteDataSource(Context context) {
        dao = new TasksDAO(context);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param context the context
     * @return the {@link TasksSqliteDataSource} instance
     */
    public static TasksSqliteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TasksSqliteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksSqliteDataSource(context);
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
    public void loadTasks(LoadTasksCallBack callBack) {
        List<TaskBean> list = dao.loadTasks();
        if (list.isEmpty()) {
            callBack.onDataNotAvailable();
        } else {
            callBack.onTasksLoaded(list);
        }
    }

    @Override
    public void getTask(String taskId, GetTaskCallBack callBack) {
        TaskBean taskBean = dao.getTaskById(taskId);
        if (taskBean == null) {
            callBack.onDataNotAvailable();
        } else {
            callBack.onTaskLoaded(taskBean);
        }
    }

    @Override
    public void clearCompletedTasks() {
        dao.deleteCompletedTasks();
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void addTask(TaskBean taskBean) {
        dao.addTask(taskBean);
    }

    @Override
    public void updateTask(TaskBean taskBean) {
        dao.updateTask(taskBean);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        completeTask(completedTaskBean.getId());
    }

    @Override
    public void completeTask(String taskId) {
        dao.updateCompleted(taskId, true);
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        activateTask(activeTaskBean.getId());
    }

    @Override
    public void activateTask(String taskId) {
        dao.updateCompleted(taskId, false);
    }

    @Override
    public void deleteTask(String taskId) {
        dao.deleteTaskById(taskId);
    }

    @Override
    public void deleteAllTasks() {
        dao.deleteTasks();
    }
}