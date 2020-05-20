package com.codearms.maoqiqi.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.VisibleForTesting
import com.codearms.maoqiqi.app.addedittask.AddEditTaskViewModel
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.statistics.StatisticsViewModel
import com.codearms.maoqiqi.app.taskdetail.TaskDetailViewModel
import com.codearms.maoqiqi.app.tasks.TasksViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 17:18
 */
class ViewModelFactory private constructor(
        private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(tasksRepository)
                    isAssignableFrom(AddEditTaskViewModel::class.java) -> AddEditTaskViewModel(tasksRepository)
                    isAssignableFrom(TaskDetailViewModel::class.java) -> TaskDetailViewModel(tasksRepository)
                    isAssignableFrom(StatisticsViewModel::class.java) -> StatisticsViewModel(tasksRepository)
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application) = INSTANCE
                ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(Injection.provideTasksRepository(
                            application.applicationContext)).also { INSTANCE = it }
                }

        @VisibleForTesting
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    @VisibleForTesting
    fun getTasksRepository(): TasksRepository {
        return tasksRepository
    }
}