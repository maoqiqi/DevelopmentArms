package com.codearms.maoqiqi.app;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.addedittask.AddEditTaskViewModel;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.statistics.StatisticsViewModel;
import com.codearms.maoqiqi.app.taskdetail.TaskDetailViewModel;
import com.codearms.maoqiqi.app.tasks.TasksViewModel;

/**
 * A creator is used to inject the product ID into the ViewModel
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 17:18
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private TasksRepository tasksRepository;

    private static volatile ViewModelFactory INSTANCE;

    private ViewModelFactory(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(Injection.provideTasksRepository(application.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public TasksRepository getTasksRepository() {
        return tasksRepository;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TasksViewModel.class)) {
            return (T) new TasksViewModel(tasksRepository);
        } else if (modelClass.isAssignableFrom(AddEditTaskViewModel.class)) {
            return (T) new AddEditTaskViewModel(tasksRepository);
        } else if (modelClass.isAssignableFrom(TaskDetailViewModel.class)) {
            return (T) new TaskDetailViewModel(tasksRepository);
        } else if (modelClass.isAssignableFrom(StatisticsViewModel.class)) {
            return (T) new StatisticsViewModel(tasksRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}