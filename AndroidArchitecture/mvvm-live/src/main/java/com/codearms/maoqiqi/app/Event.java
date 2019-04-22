package com.codearms.maoqiqi.app;

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 17:18
 */
public class Event<T> {

    private T content;

    private boolean hasBeenHandled = false;

    public Event(T content) {
        if (content == null) {
            throw new IllegalArgumentException("null values in Event are not allowed.");
        }
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public T getContent() {
        return content;
    }
}