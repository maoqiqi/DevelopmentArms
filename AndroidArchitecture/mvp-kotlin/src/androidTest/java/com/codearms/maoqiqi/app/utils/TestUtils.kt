package com.codearms.maoqiqi.app.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.annotation.IdRes
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.appcompat.widget.Toolbar

/**
 * Useful test methods common to all activities
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:15
 */
object TestUtils {

    private fun rotateToLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun rotateToPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun rotateOrientation(activity: Activity) {
        val currentOrientation = activity.resources.configuration.orientation

        when (currentOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> rotateToPortrait(activity)
            Configuration.ORIENTATION_PORTRAIT -> rotateToLandscape(activity)
            else -> rotateToLandscape(activity)
        }
    }

    /**
     * Returns the content description for the navigation button view in the toolbar.
     */
    fun getToolbarNavigationContentDescription(activity: Activity, @IdRes toolbar1: Int): String {
        val toolbar = activity.findViewById<Toolbar>(toolbar1)
        return if (toolbar != null) {
            toolbar.navigationContentDescription as String
        } else {
            throw RuntimeException("No toolbar found.")
        }
    }

    /**
     * Gets an Activity in the RESUMED stage.
     *
     *
     * This method should never be called from the Main thread.
     * In certain situations there might  be more than one Activities in RESUMED stage,
     * but only one is returned. See [ActivityLifecycleMonitor].
     */
    @Throws(IllegalStateException::class)
    fun getCurrentActivity(): Activity {
        // The array is just to wrap the Activity and be able to access it from the Runnable.
        val resumedActivity = arrayOfNulls<Activity>(1)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                resumedActivity[0] = resumedActivities.iterator().next() as Activity
            } else {
                throw IllegalStateException("No Activity in stage RESUMED")
            }
        }
        // Ugly but necessary since resumedActivity[0] needs to be declared in the outer scope and assigned in the runnable.
        return resumedActivity[0]!!
    }
}