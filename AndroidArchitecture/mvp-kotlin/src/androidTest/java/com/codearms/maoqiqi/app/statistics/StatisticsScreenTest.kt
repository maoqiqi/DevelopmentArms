package com.codearms.maoqiqi.app.statistics

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codearms.maoqiqi.app.Injection
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4::class)
class StatisticsScreenTest {

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.
     */
    @get:Rule
    var statisticsActivityTestRule = ActivityTestRule(StatisticsActivity::class.java, true, false)

    @Before
    fun setUp() {
        // Given some tasks
        Injection.provideTasksRepository(InstrumentationRegistry.getTargetContext()).deleteAllTasks()
        TasksRemoteDataSource.getInstance().addTask(TaskBean(TITLE, DESCRIPTION, false))
        TasksRemoteDataSource.getInstance().addTask(TaskBean(TITLE, DESCRIPTION, false))
        TasksRemoteDataSource.getInstance().addTask(TaskBean(TITLE, DESCRIPTION, true))

        // Lazily start the Activity from the ActivityTestRule
        statisticsActivityTestRule.launchActivity(Intent())
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun showsMessage() {
        // Check that the active and completed tasks text is displayed
        onView(ViewMatchers.withText(Matchers.containsString("未完成任务总数：2"))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(Matchers.containsString("已完成任务总数：1"))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"
    }
}