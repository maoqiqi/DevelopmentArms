package com.codearms.maoqiqi.app.data.source;

import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;

import org.reactivestreams.Publisher;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
    public Single<List<TaskBean>> loadTasks() {
        // Respond immediately with cache if available and not dirty
        if (cachedTasksMap != null && !cacheIsDirty) {
            return Flowable.fromIterable(cachedTasksMap.values()).toList();
        }

        if (cacheIsDirty) {
            return getTasksFromRemoteDataSource();
        } else {
            return Single.concat(getTasksFromLocalDataSource(), getTasksFromRemoteDataSource())
                    .filter(new Predicate<List<TaskBean>>() {
                        @Override
                        public boolean test(List<TaskBean> taskBeans) {
                            return !taskBeans.isEmpty();
                        }
                    })
                    .firstOrError();
        }
    }

    @Override
    public Flowable<TaskBean> getTask(final String taskId) {
        TaskBean cachedTaskBean = getTaskById(taskId);
        // Respond immediately with cache if available
        if (cachedTaskBean != null) {
            return Flowable.just(cachedTaskBean);
        }

        // Load from server/persisted if needed.

        if (cachedTasksMap == null) {
            cachedTasksMap = new LinkedHashMap<>();
        }

        Flowable<TaskBean> localTask = tasksLocalDataSource.getTask(taskId).doOnNext(new Consumer<TaskBean>() {
            @Override
            public void accept(TaskBean taskBean) {
                cachedTasksMap.put(taskBean.getId(), taskBean);
            }
        }).firstElement().toFlowable();
        Flowable<TaskBean> remoteTask = tasksRemoteDataSource.getTask(taskId).doOnNext(new Consumer<TaskBean>() {
            @Override
            public void accept(TaskBean taskBean) {
                cachedTasksMap.put(taskBean.getId(), taskBean);
                tasksLocalDataSource.addTask(taskBean);
            }
        });
        return Flowable.concat(localTask, remoteTask).firstElement().toFlowable();
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
    private Single<List<TaskBean>> getTasksFromRemoteDataSource() {
        if (cachedTasksMap == null) {
            cachedTasksMap = new LinkedHashMap<>();
        } else {
            cachedTasksMap.clear();
        }
        tasksLocalDataSource.deleteAllTasks();
        return tasksRemoteDataSource.loadTasks().toFlowable().flatMap(
                new Function<List<TaskBean>, Publisher<TaskBean>>() {
                    @Override
                    public Publisher<TaskBean> apply(List<TaskBean> taskBeans) {
                        return Flowable.fromIterable(taskBeans).doOnNext(new Consumer<TaskBean>() {
                            @Override
                            public void accept(TaskBean taskBean) {
                                cachedTasksMap.put(taskBean.getId(), taskBean);
                                tasksLocalDataSource.addTask(taskBean);
                            }
                        });
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        cacheIsDirty = false;
                    }
                }).toList();
    }

    private Single<List<TaskBean>> getTasksFromLocalDataSource() {
        if (cachedTasksMap == null) {
            cachedTasksMap = new LinkedHashMap<>();
        } else {
            cachedTasksMap.clear();
        }
        return tasksLocalDataSource.loadTasks().toFlowable().flatMap(
                new Function<List<TaskBean>, Publisher<TaskBean>>() {
                    @Override
                    public Publisher<TaskBean> apply(List<TaskBean> taskBeans) {
                        return Flowable.fromIterable(taskBeans).doOnNext(new Consumer<TaskBean>() {
                            @Override
                            public void accept(TaskBean taskBean) {
                                cachedTasksMap.put(taskBean.getId(), taskBean);
                            }
                        });
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        cacheIsDirty = false;
                    }
                })
                .toList();
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