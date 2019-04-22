package com.codearms.maoqiqi.app.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Prompt message mapping for the whole application.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 15:18
 */
public class MessageMap {

    public static final String CLEAR = "CLEAR";
    public static final String COMPLETE = "COMPLETE";
    public static final String ACTIVE = "ACTIVE";

    public static final String ADD = "ADD";
    public static final String EDIT = "EDIT";
    public static final String DELETE = "DELETE";

    public static final String NO_ID = "NO_ID";
    public static final String NO_DATA = "NO_DATA";
    public static final String ENTER = "ENTER";

    private static Map<String, String> MAP = new HashMap<>();

    static {
        MAP.put(CLEAR, "Completed tasks cleared");
        MAP.put(COMPLETE, "Task marked complete");
        MAP.put(ACTIVE, "Task marked active");

        MAP.put(ADD, "Task was added");
        MAP.put(EDIT, "Task was edited");
        MAP.put(DELETE, "Task was deleted");

        MAP.put(NO_ID, "TaskId cant't is null");
        MAP.put(NO_DATA, "No data");
        MAP.put(ENTER, "Please enter a title and description");
    }

    public static String get(String key) {
        String message = MAP.get(key);
        if (message == null) {
            message = "There are no matches in map!!!";
        }
        return message;
    }
}