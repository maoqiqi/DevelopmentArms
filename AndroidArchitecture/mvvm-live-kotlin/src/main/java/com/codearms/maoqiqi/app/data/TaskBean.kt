package com.codearms.maoqiqi.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Immutable model class for a Task.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:16
 * @param id  id of the task
 * @param title title of the task
 * @param description description of the task
 * @param isCompleted true if the task is completed, false if it's active
 */
@Entity(tableName = "task")
data class TaskBean @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "completed") var isCompleted: Boolean = false
) {

    val isActive: Boolean
        get() = !isCompleted

    val isEmpty: Boolean
        get() = title.isEmpty() || description.isEmpty()
}