package com.codearms.maoqiqi.app.data.source.room

import androidx.room.*
import com.codearms.maoqiqi.app.data.TaskBean

/**
 * Data Access Object for the task table.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 15:18
 */
@Dao
interface TaskDAO {

    /**
     * Select all tasks from the task table.
     *
     * @return all tasks.
     */
    @Query("select id,title,description,completed from task")
    suspend fun loadTasks(): List<TaskBean>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("select id,title,description,completed from task where id = :taskId")
    suspend fun getTaskById(taskId: String): TaskBean?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param taskBean the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskBean: TaskBean)

    /**
     * Update a task.
     *
     * @param taskBean task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    suspend fun updateTask(taskBean: TaskBean): Int

    /**
     * Update the complete status of a task
     *
     * @param taskId    the task id.
     * @param completed status to be updated
     */
    @Query("update task set completed = :completed where id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("delete from task where id = :taskId")
    suspend fun deleteTaskById(taskId: String): Int

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of tasks deleted.
     */
    @Query("delete from task where completed = 1")
    suspend fun deleteCompletedTasks(): Int

    /**
     * Delete all tasks.
     */
    @Query("delete from task")
    suspend fun deleteTasks()
}