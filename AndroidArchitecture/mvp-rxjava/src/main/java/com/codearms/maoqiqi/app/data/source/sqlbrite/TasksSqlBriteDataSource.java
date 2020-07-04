package com.codearms.maoqiqi.app.data.source.sqlbrite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.schedulers.BaseSchedulerProvider;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Use sqlbrite to create the sqlite database as the data source.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/4/1 13:52
 */
public class TasksSqlBriteDataSource implements TasksDataSource {

    @Nullable
    private static volatile TasksSqlBriteDataSource INSTANCE;

    @NonNull
    private final BriteDatabase briteDatabase;

    // Prevent direct instantiation.
    private TasksSqlBriteDataSource(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        briteDatabase = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param context the context
     * @return the {@link TasksSqlBriteDataSource} instance
     */
    public static TasksSqlBriteDataSource getInstance(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            synchronized (TasksSqlBriteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksSqlBriteDataSource(context, schedulerProvider);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    private Function<Cursor, TaskBean> cursorTaskBeanFunc = new Function<Cursor, TaskBean>() {
        @Override
        public TaskBean apply(Cursor cursor) throws Exception {
            TaskBean taskBean = new TaskBean();
            taskBean.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            taskBean.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            taskBean.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            taskBean.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1);
            return taskBean;
        }
    };

    /**
     * Select all tasks from the task table.
     *
     * @return all tasks.
     */
    @Override
    public Flowable<List<TaskBean>> loadTasks() {
        String sql = "select id,title,description,completed from task";
        return briteDatabase.createQuery("task", sql)
                .mapToList(cursorTaskBeanFunc)
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Override
    public Flowable<TaskBean> getTask(String taskId) {
        String sql = "select id,title,description,completed from task where id = ?";
        return briteDatabase.createQuery("task", sql, taskId)
                .mapToOne(cursorTaskBeanFunc)
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Delete all completed tasks from the table.
     */
    @Override
    public void clearCompletedTasks() {
        briteDatabase.delete("task", "completed = ?", "1");
    }

    @Override
    public void refreshTasks() {

    }

    /**
     * Insert a task in the database.
     *
     * @param taskBean the task to be inserted.
     */
    @Override
    public void addTask(final TaskBean taskBean) {
        ContentValues values = new ContentValues();
        values.put("id", taskBean.getId());
        values.put("title", taskBean.getTitle());
        values.put("description", taskBean.getDescription());
        values.put("completed", taskBean.isCompleted() ? 1 : 0);

        briteDatabase.insert("task", values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Update a task.
     *
     * @param taskBean task to be updated
     */
    @Override
    public void updateTask(final TaskBean taskBean) {
        ContentValues values = new ContentValues();
        values.put("title", taskBean.getTitle());
        values.put("description", taskBean.getDescription());
        values.put("completed", taskBean.isCompleted() ? 1 : 0);

        briteDatabase.update("task", values, "id = ?", taskBean.getId());
    }

    /**
     * Update the complete is true of a task
     *
     * @param completedTaskBean the task
     */
    @Override
    public void completeTask(TaskBean completedTaskBean) {
        completeTask(completedTaskBean.getId());
    }

    /**
     * Update the complete is true of a task
     *
     * @param taskId the task id.
     */
    @Override
    public void completeTask(final String taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", true);
        briteDatabase.update("task", values, "id = ?", taskId);
    }

    /**
     * Update the complete is false of a task
     *
     * @param activeTaskBean the task
     */
    @Override
    public void activateTask(TaskBean activeTaskBean) {
        activateTask(activeTaskBean.getId());
    }

    /**
     * Update the complete is false of a task
     *
     * @param taskId the task id.
     */
    @Override
    public void activateTask(final String taskId) {
        ContentValues values = new ContentValues();
        values.put("completed", false);
        briteDatabase.update("task", values, "id = ?", taskId);
    }

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     */
    @Override
    public void deleteTask(final String taskId) {
        briteDatabase.delete("task", "id = ?", taskId);
    }

    /**
     * Delete all tasks.
     */
    @Override
    public void deleteAllTasks() {
        briteDatabase.delete("task", null);
    }
}