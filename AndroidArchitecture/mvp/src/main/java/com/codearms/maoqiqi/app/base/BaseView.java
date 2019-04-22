package com.codearms.maoqiqi.app.base;

public interface BaseView<T> {

    void setPresenter(T presenter);

    boolean isActive();
}