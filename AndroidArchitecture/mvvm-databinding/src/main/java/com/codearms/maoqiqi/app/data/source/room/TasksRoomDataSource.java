package com.codearms.maoqiqi.app.data.source.room;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.AppExecutors;

import java.util.List;

/**
 * Use room to create the sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
public class TasksRoomDataSource implements TasksDataSource {

    private static volatile TasksRoomDataSource INSTANCE;

    private final TasksDAO dao;
    private final AppExecutors appExecutors;

    private TasksRoomDataSource(Context context, AppExecutors appExecutors) {
        this.dao = TaskDataBase.getInstance(context).tasksDAO();
        this.appExecutors = appExecutors;
    }

    public static TasksRoomDataSource getInstance(Context context, AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (TasksRoomDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRoomDataSource(context, appExecutors);
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
    public void loadTasks(final LoadTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<TaskBean> list = dao.loadTasks();
                appExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (list.isEmpty()) {
                            callBack.onDataNotAvailable();
                        } else {
                            callBack.onTasksLoaded(list);
                        }
                    }
                });
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void getTask(final String taskId, final GetTaskCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final TaskBean taskBean = dao.getTaskById(taskId);
                appExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (taskBean == null) {
                            callBack.onDataNotAvailable();
                        } else {
                            callBack.onTaskLoaded(taskBean);
                        }
                    }
                });
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void clearCompletedTasks() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteCompletedTasks();
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void addTask(final TaskBean taskBean) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.addTask(taskBean);
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void updateTask(final TaskBean taskBean) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.updateTask(taskBean);
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        completeTask(completedTaskBean.getId());
    }

    @Override
    public void completeTask(final String taskId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.updateCompleted(taskId, true);
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        activateTask(activeTaskBean.getId());
    }

    @Override
    public void activateTask(final String taskId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.updateCompleted(taskId, false);
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void deleteTask(final String taskId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteTaskById(taskId);
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void deleteAllTasks() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dao.deleteTasks();
            }
        };
        appExecutors.getDiskIO().execute(runnable);
    }
}