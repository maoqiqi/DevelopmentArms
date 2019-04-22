package com.codearms.maoqiqi.app.tasks;

/**
 * Used with the filter spinner in the tasks list.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 10:21
 */
public enum TasksFilterType {

    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS
}