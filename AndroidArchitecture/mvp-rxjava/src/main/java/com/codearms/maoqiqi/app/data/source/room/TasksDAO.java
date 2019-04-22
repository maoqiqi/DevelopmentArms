package com.codearms.maoqiqi.app.data.source.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.codearms.maoqiqi.app.data.TaskBean;

import java.util.List;

/**
 * Data Access Object for the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
@Dao
public interface TasksDAO {

    /**
     * Select all tasks from the task table.
     *
     * @return all tasks.
     */
    @Query("select id,title,description,completed from task")
    List<TaskBean> loadTasks();

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("select id,title,description,completed from task where id = :taskId")
    TaskBean getTaskById(String taskId);

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param taskBean the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTask(TaskBean taskBean);

    /**
     * Update a task.
     *
     * @param taskBean task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    int updateTask(TaskBean taskBean);

    /**
     * Update the complete status of a task
     *
     * @param taskId    the task id.
     * @param completed status to be updated
     */
    @Query("update task set completed = :completed where id = :taskId")
    void updateCompleted(String taskId, boolean completed);

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("delete from task where id = :taskId")
    int deleteTaskById(String taskId);

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("delete from task where completed = 1")
    int deleteCompletedTasks();

    /**
     * Delete all tasks.
     */
    @Query("delete from task")
    void deleteTasks();
}