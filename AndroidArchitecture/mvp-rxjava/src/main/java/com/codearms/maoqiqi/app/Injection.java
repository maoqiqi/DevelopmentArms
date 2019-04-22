package com.codearms.maoqiqi.app;

import android.content.Context;

import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource;
import com.codearms.maoqiqi.app.data.source.room.TasksRoomDataSource;
import com.codearms.maoqiqi.app.utils.AppExecutors;

/**
 * Enables injection of mock implementations for {@link TasksDataSource} at compile time.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/14 16:42
 */
public class Injection {

    public static TasksRepository provideTasksRepository(Context context) {
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksRoomDataSource.getInstance(context, new AppExecutors()));
    }
}