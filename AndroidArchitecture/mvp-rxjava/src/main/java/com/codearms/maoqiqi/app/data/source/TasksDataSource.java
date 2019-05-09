package com.codearms.maoqiqi.app.data.source;

import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.List;

import io.reactivex.Single;

/**
 * Main entry point for accessing tasks data.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
public interface TasksDataSource {

    Single<List<TaskBean>> loadTasks();

    Single<TaskBean> getTask(String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void addTask(TaskBean taskBean);

    void updateTask(final TaskBean taskBean);

    void completeTask(TaskBean completedTaskBean);

    void completeTask(String taskId);

    void activateTask(TaskBean activeTaskBean);

    void activateTask(String taskId);

    void deleteTask(String taskId);

    void deleteAllTasks();
}