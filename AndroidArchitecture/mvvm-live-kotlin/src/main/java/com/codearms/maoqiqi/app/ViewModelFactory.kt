package com.codearms.maoqiqi.app

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedStateRegistryOwner
import com.codearms.maoqiqi.app.addedittask.AddEditTaskViewModel
import com.codearms.maoqiqi.app.data.source.TaskRepository
import com.codearms.maoqiqi.app.statistics.StatisticsViewModel
import com.codearms.maoqiqi.app.taskdetail.TaskDetailViewModel
import com.codearms.maoqiqi.app.tasks.TasksViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 17:18
 */
class ViewModelFactory(
    private val tasksRepository: TaskRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
        with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(tasksRepository)
                isAssignableFrom(AddEditTaskViewModel::class.java) -> AddEditTaskViewModel(tasksRepository)
                isAssignableFrom(TaskDetailViewModel::class.java) -> TaskDetailViewModel(tasksRepository)
                isAssignableFrom(StatisticsViewModel::class.java) -> StatisticsViewModel(tasksRepository)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}