package com.codearms.maoqiqi.app.tasks;

/**
 * Defines the navigation actions that can be called from the task list screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 12:30
 */
public interface TaskItemListener {

    void onOpenTaskDetails(String taskId);
}