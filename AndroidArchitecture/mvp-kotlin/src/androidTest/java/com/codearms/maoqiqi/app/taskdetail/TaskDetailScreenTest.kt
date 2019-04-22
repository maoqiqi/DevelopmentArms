package com.codearms.maoqiqi.app.taskdetail

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource
import com.codearms.maoqiqi.app.utils.TestUtils
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4::class)
class TaskDetailScreenTest {

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.
     */
    @get:Rule
    var taskDetailActivityTestRule = ActivityTestRule(TaskDetailActivity::class.java, true, false)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadActiveTask() {
        startActivity(ACTIVE_TASK)

        // Check that the task title and description are displayed
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)))
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)))
        onView(ViewMatchers.withId(R.id.cbComplete)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isChecked())))
    }

    @Test
    fun loadCompletedTask() {
        startActivity(COMPLETED_TASK)

        // Check that the task title and description are displayed
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)))
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)))
        onView(ViewMatchers.withId(R.id.cbComplete)).check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun rotation() {
        loadActiveTask()

        // Check delete menu item is displayed and is unique
        onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Rotate activity
        TestUtils.rotateOrientation(taskDetailActivityTestRule.activity)

        // Check that the task is shown
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)))
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)))

        // Check delete menu item is displayed and is unique
        onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun startActivity(taskBean: TaskBean) {
        TasksRepository.destroyInstance()
        TasksRemoteDataSource.getInstance().addTask(taskBean)

        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        val startIntent = Intent()
        startIntent.putExtra(TasksActivity.EXTRA_TASK_ID, taskBean.id)
        taskDetailActivityTestRule.launchActivity(startIntent)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"

        private val ACTIVE_TASK = TaskBean(TITLE, DESCRIPTION, false)
        private val COMPLETED_TASK = TaskBean(TITLE, DESCRIPTION, true)
    }
}