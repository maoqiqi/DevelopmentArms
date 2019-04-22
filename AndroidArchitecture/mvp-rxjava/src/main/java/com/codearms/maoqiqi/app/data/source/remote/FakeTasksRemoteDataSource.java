package com.codearms.maoqiqi.app.data.source.remote;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implement a fast access to the server API interface.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 15:15
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, TaskBean> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    private FakeTasksRemoteDataSource() {

    }

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (FakeTasksRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FakeTasksRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void loadTasks(LoadTasksCallBack callBack) {
        callBack.onTasksLoaded(new ArrayList<>(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void getTask(String taskId, GetTaskCallBack callBack) {
        TaskBean taskBean = TASKS_SERVICE_DATA.get(taskId);
        callBack.onTaskLoaded(taskBean);
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, TaskBean>> iterator = TASKS_SERVICE_DATA.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TaskBean> entry = iterator.next();
            if (entry.getValue().isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void addTask(TaskBean taskBean) {
        TASKS_SERVICE_DATA.put(taskBean.getId(), taskBean);
    }

    @Override
    public void updateTask(TaskBean taskBean) {
        TASKS_SERVICE_DATA.put(taskBean.getId(), taskBean);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        completeTask(completedTaskBean.getId());
    }

    @Override
    public void completeTask(String taskId) {
        TaskBean taskBean = TASKS_SERVICE_DATA.get(taskId);
        if (taskBean != null) {
            taskBean.setCompleted(true);
            TASKS_SERVICE_DATA.put(taskBean.getId(), taskBean);
        }
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        activateTask(activeTaskBean.getId());
    }

    @Override
    public void activateTask(String taskId) {
        TaskBean taskBean = TASKS_SERVICE_DATA.get(taskId);
        if (taskBean != null) {
            taskBean.setCompleted(false);
            TASKS_SERVICE_DATA.put(taskBean.getId(), taskBean);
        }
    }

    @Override
    public void deleteTask(String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }
}