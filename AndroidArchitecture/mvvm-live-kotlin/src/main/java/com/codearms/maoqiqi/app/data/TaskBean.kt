package com.codearms.maoqiqi.app.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Immutable model class for a Task.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:16
 */
@Entity(tableName = "task")
class TaskBean {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id = ""

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "completed")
    var isCompleted: Boolean = false

    val isActive: Boolean
        get() = !isCompleted

    constructor()

    /**
     * Use this constructor to create a new Task.
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is completed, false if it's active
     */
    @Ignore
    constructor(title: String, description: String, completed: Boolean) :
            this(UUID.randomUUID().toString(), title, description, completed)

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param id          id of the task
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is completed, false if it's active
     */
    @Ignore
    constructor(id: String, title: String, description: String, completed: Boolean) {
        this.id = id
        this.title = title
        this.description = description
        this.isCompleted = completed
    }

    override fun toString(): String {
        return "TaskBean(id='$id', title=$title, description=$description, isCompleted=$isCompleted)"
    }
}