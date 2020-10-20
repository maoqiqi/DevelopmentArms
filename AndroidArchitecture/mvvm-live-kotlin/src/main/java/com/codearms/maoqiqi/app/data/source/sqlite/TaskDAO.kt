package com.codearms.maoqiqi.app.data.source.sqlite

import android.content.Context
import com.codearms.maoqiqi.app.data.TaskBean
import java.util.*

/**
 * Data Access Object for the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
internal class TaskDAO(context: Context) {

    private val helper: DatabaseHelper = DatabaseHelper(context)

    /**
     * Select all tasks from the task table.
     *
     * @return all tasks.
     */
    fun loadTasks(): List<TaskBean> {
        val list = ArrayList<TaskBean>()

        val db = helper.readableDatabase
        val sql = "select id,title,description,completed from task"
        val cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val taskBean = TaskBean()
            taskBean.id = cursor.getString(cursor.getColumnIndex("id"))
            taskBean.title = cursor.getString(cursor.getColumnIndex("title"))
            taskBean.description = cursor.getString(cursor.getColumnIndex("description"))
            taskBean.isCompleted = cursor.getInt(cursor.getColumnIndex("completed")) == 1
            list.add(taskBean)
        }
        cursor.close()
        db.close()
        return list
    }

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    fun getTaskById(taskId: String): TaskBean? {
        var taskBean: TaskBean? = null

        val db = helper.readableDatabase
        val sql = "select id,title,description,completed from task where id = ?"
        val cursor = db.rawQuery(sql, arrayOf(taskId))
        if (cursor.moveToNext()) {
            taskBean = TaskBean().apply {
                id = cursor.getString(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                description = cursor.getString(cursor.getColumnIndex("description"))
                isCompleted = cursor.getInt(cursor.getColumnIndex("completed")) == 1
            }
        }
        cursor.close()
        db.close()
        return taskBean
    }

    /**
     * Insert a task in the database.
     *
     * @param taskBean the task to be inserted.
     */
    fun addTask(taskBean: TaskBean) {
        val db = helper.writableDatabase
        val sql = "insert into task(id,title,description,completed) values(?,?,?,?)"
        db.execSQL(sql, arrayOf(taskBean.id, taskBean.title, taskBean.description, if (taskBean.isCompleted) 1 else 0))
        db.close()
    }

    /**
     * Update a task.
     *
     * @param taskBean task to be updated
     */
    fun updateTask(taskBean: TaskBean) {
        val db = helper.writableDatabase
        val sql = "update task set title=?,description=?,completed=? where id=?"
        db.execSQL(sql, arrayOf(taskBean.title, taskBean.description, if (taskBean.isCompleted) 1 else 0, taskBean.id))
        db.close()
    }

    /**
     * Update the complete status of a task
     *
     * @param taskId    the task id.
     * @param completed status to be updated
     */
    fun updateCompleted(taskId: String, completed: Boolean) {
        val db = helper.writableDatabase
        val sql = "update task set completed = ? where id = ?"
        db.execSQL(sql, arrayOf(if (completed) 1 else 0, taskId))
        db.close()
    }

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     */
    fun deleteTaskById(taskId: String) {
        val db = helper.writableDatabase
        val sql = "delete from task where id = ?"
        db.execSQL(sql, arrayOf(taskId))
        db.close()
    }

    /**
     * Delete all completed tasks from the table.
     */
    fun deleteCompletedTasks() {
        val db = helper.writableDatabase
        val sql = "delete from task where completed = 1"
        db.execSQL(sql)
        db.close()
    }

    /**
     * Delete all tasks.
     */
    fun deleteTasks() {
        val db = helper.writableDatabase
        val sql = "delete from task"
        db.execSQL(sql)
        db.close()
    }
}