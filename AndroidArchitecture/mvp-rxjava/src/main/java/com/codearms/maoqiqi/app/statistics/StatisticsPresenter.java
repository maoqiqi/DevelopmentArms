package com.codearms.maoqiqi.app.statistics;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Listens to user actions from the UI ({@link StatisticsFragment}), retrieves the data and updates the UI as required.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:48
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private TasksRepository tasksRepository;
    private StatisticsContract.View statisticsView;
    private BaseSchedulerProvider schedulerProvider;

    @NonNull
    private CompositeDisposable compositeDisposable;

    StatisticsPresenter(TasksRepository tasksRepository, StatisticsContract.View statisticsView, BaseSchedulerProvider schedulerProvider) {
        this.tasksRepository = tasksRepository;
        this.statisticsView = statisticsView;
        this.statisticsView.setPresenter(this);
        this.schedulerProvider = schedulerProvider;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        loadStatistics();
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void loadStatistics() {
        Flowable<TaskBean> f = tasksRepository.loadTasks().toFlowable().flatMap(new Function<List<TaskBean>, Publisher<TaskBean>>() {
            @Override
            public Publisher<TaskBean> apply(List<TaskBean> taskBeans) {
                return Flowable.fromIterable(taskBeans);
            }
        });
        Flowable<Long> completedTasks = f.filter(new Predicate<TaskBean>() {
            @Override
            public boolean test(TaskBean taskBean) {
                return taskBean.isCompleted();
            }
        }).count().toFlowable();
        Flowable<Long> activeTasks = f.filter(new Predicate<TaskBean>() {
            @Override
            public boolean test(TaskBean taskBean) {
                return taskBean.isActive();
            }
        }).count().toFlowable();
        Disposable disposable = Flowable.zip(completedTasks, activeTasks,
                new BiFunction<Long, Long, Pair>() {
                    @Override
                    public Pair apply(Long completed, Long active) {
                        return Pair.create(active, completed);
                    }
                })
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(new Consumer<Pair>() {
                    @Override
                    public void accept(Pair pair) {
                        long activeTasks = (long) pair.first;
                        long completedTasks = (long) pair.second;
                        statisticsView.showStatistics((int) activeTasks, (int) completedTasks);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        statisticsView.showMessage(MessageMap.NO_DATA);
                    }
                });
        compositeDisposable.add(disposable);
    }
}