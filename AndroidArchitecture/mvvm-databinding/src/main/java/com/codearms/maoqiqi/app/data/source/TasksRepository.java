package com.codearms.maoqiqi.app.data.source;

import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Load tasks from the data sources into a cache.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 16:20
 */
public class TasksRepository implements TasksDataSource {

    private static volatile TasksRepository INSTANCE = null;

    private final TasksDataSource tasksRemoteDataSource;
    private final TasksDataSource tasksLocalDataSource;

    Map<String, TaskBean> cachedTasksMap;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    private boolean cacheIsDirty = false;

    private TasksRepository(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        this.tasksRemoteDataSource = tasksRemoteDataSource;
        this.tasksLocalDataSource = tasksLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            synchronized (TasksRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * On the next call, force {@link #getInstance(TasksDataSource, TasksDataSource)} to create a new instance.
     */
    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is available first.
     */
    @Override
    public void loadTasks(final LoadTasksCallBack callBack) {
        // Respond immediately with cache if available and not dirty
        if (cachedTasksMap != null && !cacheIsDirty) {
            callBack.onTasksLoaded(new ArrayList<>(cachedTasksMap.values()));
            return;
        }

        EspressoIdlingResource.increment();

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callBack);
        } else {
            // Query the local storage if available. If not, query the network.
            tasksLocalDataSource.loadTasks(new LoadTasksCallBack() {
                @Override
                public void onTasksLoaded(List<TaskBean> taskBeanList) {
                    refreshCache(taskBeanList);
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement();
                    }
                    callBack.onTasksLoaded(new ArrayList<>(cachedTasksMap.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callBack);
                }
            });
        }
    }

    @Override
    public void getTask(final String taskId, final GetTaskCallBack callBack) {
        TaskBean cachedTaskBean = getTaskById(taskId);
        // Respond immediately with cache if available
        if (cachedTaskBean != null) {
            callBack.onTaskLoaded(cachedTaskBean);
            return;
        }

        EspressoIdlingResource.increment();

        // Load from server/persisted if needed.

        // Query the local storage if available. If not, query the network.
        tasksLocalDataSource.getTask(taskId, new GetTaskCallBack() {
            @Override
            public void onTaskLoaded(TaskBean taskBean) {
                refreshCache(taskBean);
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }
                callBack.onTaskLoaded(taskBean);
            }

            @Override
            public void onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, new GetTaskCallBack() {
                    @Override
                    public void onTaskLoaded(TaskBean taskBean) {
                        refreshCache(taskBean);
                        tasksLocalDataSource.addTask(taskBean);
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                            EspressoIdlingResource.decrement();
                        }
                        callBack.onTaskLoaded(taskBean);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                            EspressoIdlingResource.decrement();
                        }
                        callBack.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks();
        tasksLocalDataSource.clearCompletedTasks();

        if (cachedTasksMap == null || cachedTasksMap.isEmpty()) return;

        Iterator<Map.Entry<String, TaskBean>> iterator = cachedTasksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TaskBean> entry = iterator.next();
            if (entry.getValue().isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        cacheIsDirty = true;
    }

    @Override
    public void addTask(TaskBean taskBean) {
        tasksRemoteDataSource.addTask(taskBean);
        tasksLocalDataSource.addTask(taskBean);
        refreshCache(taskBean);
    }

    @Override
    public void updateTask(TaskBean taskBean) {
        tasksRemoteDataSource.updateTask(taskBean);
        tasksLocalDataSource.updateTask(taskBean);
        refreshCache(taskBean);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        if (completedTaskBean == null) return;

        tasksRemoteDataSource.completeTask(completedTaskBean);
        tasksLocalDataSource.completeTask(completedTaskBean);

        completedTaskBean.setCompleted(true);
        refreshCache(completedTaskBean);
    }

    @Override
    public void completeTask(String taskId) {
        if (taskId != null) completeTask(getTaskById(taskId));
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        if (activeTaskBean == null) return;

        tasksRemoteDataSource.activateTask(activeTaskBean);
        tasksLocalDataSource.activateTask(activeTaskBean);

        activeTaskBean.setCompleted(false);
        refreshCache(activeTaskBean);
    }

    @Override
    public void activateTask(String taskId) {
        if (taskId != null) activateTask(getTaskById(taskId));
    }

    @Override
    public void deleteTask(String taskId) {
        tasksRemoteDataSource.deleteTask(taskId);
        tasksLocalDataSource.deleteTask(taskId);
        cachedTasksMap.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks();
        tasksLocalDataSource.deleteAllTasks();
        if (cachedTasksMap != null) cachedTasksMap.clear();
    }

    /**
     * Getting new data from the network.
     */
    private void getTasksFromRemoteDataSource(final LoadTasksCallBack callBack) {
        tasksRemoteDataSource.loadTasks(new LoadTasksCallBack() {
            @Override
            public void onTasksLoaded(List<TaskBean> taskBeanList) {
                refreshCache(taskBeanList);
                refreshLocalDataSource(taskBeanList);
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }
                callBack.onTasksLoaded(new ArrayList<>(cachedTasksMap.values()));
            }

            @Override
            public void onDataNotAvailable() {
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement();
                }
                callBack.onDataNotAvailable();
            }
        });
    }

    /**
     * Refresh cache data
     */
    private void refreshCache(List<TaskBean> taskBeanList) {
        if (cachedTasksMap == null) {
            cachedTasksMap = new LinkedHashMap<>();
        } else {
            cachedTasksMap.clear();
        }

        for (TaskBean taskBean : taskBeanList) {
            cachedTasksMap.put(taskBean.getId(), taskBean);
        }
        cacheIsDirty = false;
    }

    /**
     * Refresh cache data
     */
    private void refreshCache(TaskBean taskBean) {
        // Do in memory cache update to keep the app UI up to date
        if (cachedTasksMap == null) {
            cachedTasksMap = new LinkedHashMap<>();
        }
        cachedTasksMap.put(taskBean.getId(), taskBean);
    }

    /**
     * Refresh local source data
     */
    private void refreshLocalDataSource(List<TaskBean> taskBeanList) {
        tasksLocalDataSource.deleteAllTasks();
        for (TaskBean taskBean : taskBeanList) {
            tasksLocalDataSource.addTask(taskBean);
        }
    }

    /**
     * Getting a task by id
     */
    @VisibleForTesting
    TaskBean getTaskById(String taskId) {
        if (cachedTasksMap == null || cachedTasksMap.isEmpty()) {
            return null;
        } else {
            return cachedTasksMap.get(taskId);
        }
    }
}