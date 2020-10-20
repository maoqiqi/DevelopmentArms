package com.codearms.maoqiqi.app.utils

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codearms.maoqiqi.app.Injection
import com.codearms.maoqiqi.app.ViewModelFactory

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is  performed by the `fragmentManager`.
 */
fun AppCompatActivity.add(@IdRes frameId: Int, fragment: androidx.fragment.app.Fragment) {
    supportFragmentManager.transact { add(frameId, fragment) }
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun androidx.fragment.app.FragmentManager.transact(action: androidx.fragment.app.FragmentTransaction.() -> Unit) {
    beginTransaction().apply { action() }.commit()
}

fun AppCompatActivity.setToolbar(@IdRes resId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(resId))
    supportActionBar?.run { action() }
}

fun AppCompatActivity.getViewModelFactory(): ViewModelFactory =
    ViewModelFactory(Injection.provideTasksRepository(this), this)

fun Fragment.getViewModelFactory(): ViewModelFactory =
    ViewModelFactory(Injection.provideTasksRepository(requireContext()), this)