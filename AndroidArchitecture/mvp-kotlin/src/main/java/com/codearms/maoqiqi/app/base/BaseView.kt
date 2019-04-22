package com.codearms.maoqiqi.app.base

interface BaseView<T> {

    var presenter: T

    val isActive: Boolean
}