package com.codearms.maoqiqi.app.data.source.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 14:24
 */
final class TasksDAO {

    private final DatabaseHelper helper;

    TasksDAO(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    /**
     * Select all tasks from the task table.
     *
     * @return all tasks.
     */
    List<TaskBean> loadTasks() {
        List<TaskBean> list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select id,title,description,completed from task";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            TaskBean taskBean = new TaskBean();
            taskBean.setId(cursor.getString(cursor.getColumnIndex("id")));
            taskBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            taskBean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            taskBean.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")) == 1);
            list.add(taskBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    TaskBean getTaskById(String taskId) {
        TaskBean taskBean = null;

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select id,title,description,completed from task where id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{taskId});
        if (cursor.moveToNext()) {
            taskBean = new TaskBean();
            taskBean.setId(cursor.getString(cursor.getColumnIndex("id")));
            taskBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            taskBean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            taskBean.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")) == 1);
        }
        cursor.close();
        db.close();
        return taskBean;
    }

    /**
     * Insert a task in the database.
     *
     * @param taskBean the task to be inserted.
     */
    void addTask(TaskBean taskBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into task(id,title,description,completed) values(?,?,?,?)";
        db.execSQL(sql, new Object[]{taskBean.getId(), taskBean.getTitle(),
                taskBean.getDescription(), taskBean.isCompleted() ? 1 : 0});
        db.close();
    }

    /**
     * Update a task.
     *
     * @param taskBean task to be updated
     */
    void updateTask(TaskBean taskBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "update task set title=?,description=?,completed=? where id=?";
        db.execSQL(sql, new Object[]{taskBean.getTitle(), taskBean.getDescription(),
                taskBean.isCompleted() ? 1 : 0, taskBean.getId()});
        db.close();
    }

    /**
     * Update the complete status of a task
     *
     * @param taskId    the task id.
     * @param completed status to be updated
     */
    void updateCompleted(String taskId, boolean completed) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "update task set completed = ? where id = ?";
        db.execSQL(sql, new Object[]{completed ? 1 : 0, taskId});
        db.close();
    }

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     */
    void deleteTaskById(String taskId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from task where id = ?";
        db.execSQL(sql, new Object[]{taskId});
        db.close();
    }

    /**
     * Delete all completed tasks from the table.
     */
    void deleteCompletedTasks() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from task where completed = 1";
        db.execSQL(sql);
        db.close();
    }

    /**
     * Delete all tasks.
     */
    void deleteTasks() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from task";
        db.execSQL(sql);
        db.close();
    }
}