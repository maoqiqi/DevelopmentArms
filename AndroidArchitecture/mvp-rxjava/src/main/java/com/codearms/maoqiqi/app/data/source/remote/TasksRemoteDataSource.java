package com.codearms.maoqiqi.app.data.source.remote;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.internal.operators.flowable.FlowableSingleSingle;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Access the server API interface data as a data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:42
 */
public class TasksRemoteDataSource implements TasksDataSource {

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static TasksRemoteDataSource INSTANCE;

    private static final Map<String, TaskBean> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }

    private TasksRemoteDataSource() {

    }

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TasksRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Single<List<TaskBean>> loadTasks() {
        return Flowable.fromIterable(TASKS_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList();
    }

    @Override
    public Single<TaskBean> getTask(String taskId) {
        final TaskBean taskBean = TASKS_SERVICE_DATA.get(taskId);
        if (taskBean != null) {
            return Single.just(taskBean).delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS);
        } else {
            return RxJavaPlugins.onAssembly(new FlowableSingleSingle<>(Flowable.<TaskBean>empty(), null));
        }
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

    private static void addTask(String title, String description) {
        TaskBean taskBean = new TaskBean(title, description, false);
        TASKS_SERVICE_DATA.put(taskBean.getId(), taskBean);
    }
}