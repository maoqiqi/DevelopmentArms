package com.codearms.maoqiqi.app.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.codearms.maoqiqi.app.ViewModelFactory

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is  performed by the `fragmentManager`.
 */
fun AppCompatActivity.add(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager.transact { add(frameId, fragment) }
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply { action() }.commit()
}

fun AppCompatActivity.setToolbar(@IdRes resId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(resId))
    supportActionBar?.run { action() }
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)