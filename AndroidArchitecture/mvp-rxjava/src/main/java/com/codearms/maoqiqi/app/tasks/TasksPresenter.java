package com.codearms.maoqiqi.app.tasks;

import androidx.annotation.NonNull;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Listens to user actions from the UI ({@link TasksFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository tasksRepository;
    private final TasksContract.View tasksView;
    private final BaseSchedulerProvider schedulerProvider;

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private boolean firstLoad = true;

    @NonNull
    private CompositeDisposable compositeDisposable;

    TasksPresenter(TasksRepository tasksRepository, TasksContract.View tasksView, BaseSchedulerProvider schedulerProvider) {
        this.tasksRepository = tasksRepository;
        this.tasksView = tasksView;
        this.schedulerProvider = schedulerProvider;
        this.tasksView.setPresenter(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        loadTasks(false);
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        // The first load forcing update
        loadTasks(forceUpdate || firstLoad, true);
        firstLoad = false;
    }

    /**
     * load tasks
     *
     * @param forceUpdate Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoading Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoading) {
        if (showLoading) tasksView.setLoadingIndicator(true);

        // Notice:Tests are used to clear the data each time.
        // if (forceUpdate) tasksRepository.refreshTasks();

        Disposable disposable = tasksRepository.loadTasks().toFlowable().flatMap(
                new Function<List<TaskBean>, Publisher<TaskBean>>() {
                    @Override
                    public Publisher<TaskBean> apply(List<TaskBean> taskBeans) {
                        return Flowable.fromIterable(taskBeans);
                    }
                })
                .filter(new Predicate<TaskBean>() {
                    @Override
                    public boolean test(TaskBean taskBean) {
                        switch (currentFiltering) {
                            case ACTIVE_TASKS:
                                return taskBean.isActive();
                            case COMPLETED_TASKS:
                                return taskBean.isCompleted();
                            case ALL_TASKS:
                            default:
                                return true;
                        }
                    }
                })
                .toList()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(new Consumer<List<TaskBean>>() {
                    @Override
                    public void accept(List<TaskBean> taskBeans) {
                        if (showLoading) tasksView.setLoadingIndicator(false);
                        processTasks(taskBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (showLoading) tasksView.setLoadingIndicator(false);
                        tasksView.showNoTasks();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void processTasks(List<TaskBean> taskBeanList) {
        if (taskBeanList.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            tasksView.showNoTasks();
        } else {
            // Set the filter label's text.
            tasksView.showFilterLabel();
            // Show the list of tasks
            tasksView.showTasks(taskBeanList);
        }
    }

    @Override
    public void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        tasksView.showMessage(MessageMap.CLEAR);
        loadTasks(false, false);
    }

    @Override
    public void completeTask(TaskBean completedTaskBean) {
        tasksRepository.completeTask(completedTaskBean);
        tasksView.showMessage(MessageMap.COMPLETE);
        loadTasks(false, false);
    }

    @Override
    public void activateTask(TaskBean activeTaskBean) {
        tasksRepository.activateTask(activeTaskBean);
        tasksView.showMessage(MessageMap.ACTIVE);
        loadTasks(false, false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == TasksActivity.REQUEST_CODE) {
            switch (resultCode) {
                case TasksActivity.RESULT_ADD_TASK:
                    tasksView.showMessage(MessageMap.ADD);
                    break;
                case TasksActivity.RESULT_EDIT_TASK:
                    tasksView.showMessage(MessageMap.EDIT);
                    break;
                case TasksActivity.RESULT_DELETE_TASK:
                    tasksView.showMessage(MessageMap.DELETE);
                    break;
            }
        }
    }

    /**
     * Sets the current task filtering type.
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;
    }

    /**
     * Gets the current task filtering type.
     */
    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }
}