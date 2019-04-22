package com.codearms.maoqiqi.app.addedittask

import android.content.Intent
import android.content.res.Resources
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import android.view.View
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.data.TaskBean
import com.codearms.maoqiqi.app.data.source.TasksRepository
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource
import com.codearms.maoqiqi.app.tasks.TasksActivity
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource
import com.codearms.maoqiqi.app.utils.TestUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the add task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4::class)
class AddEditTaskScreenTest {

    private val taskBean = TaskBean(ID, TITLE, DESCRIPTION, false)

    /**
     * [IntentsTestRule] is an [ActivityTestRule] which init and releases Espresso Intents before and after each test run.
     */
    @get:Rule
    var addEditTaskActivityTestRule = ActivityTestRule(AddEditTaskActivity::class.java, false, false)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addEmptyTask() {
        // Launch activity to add a new task
        launchNewTaskActivity(null)

        // Add invalid title and description combination
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.clearText())
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.clearText())

        // Try to save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click())

        // Verify that the activity is still displayed (a correct task would close it).
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun rotation() {
        // Launch activity to add a new task
        launchNewTaskActivity(null)

        // Check that the toolbar shows the correct title
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.add_task)))

        // Rotate activity
        TestUtils.rotateOrientation(addEditTaskActivityTestRule.activity)

        // Check that the toolbar title is persisted
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.add_task)))
    }

    @Test
    fun editTask() {
        // Put a task in the repository and start the activity to edit it
        TasksRepository.destroyInstance()
        TasksRemoteDataSource.getInstance().addTask(taskBean)
        launchNewTaskActivity(taskBean.id)

        // Check that the toolbar shows the correct title
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.edit_task)))

        // Rotate activity
        TestUtils.rotateOrientation(addEditTaskActivityTestRule.activity)

        // Check that the toolbar title is persisted
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.edit_task)))

        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.withText(taskBean.title)))
        onView(ViewMatchers.withId(R.id.editTextDescription)).check(ViewAssertions.matches(ViewMatchers.withText(taskBean.description)))
    }

    /**
     * Launch AddEditTaskActivity
     *
     * @param taskId is null if used to add a new task, otherwise it edits the task.
     */
    private fun launchNewTaskActivity(taskId: String?) {
        val intent = Intent()
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId)
        addEditTaskActivityTestRule.launchActivity(intent)
    }

    companion object {
        private const val ID = "ID"
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"

        /**
         * Matches the toolbar title with a specific string resource.
         *
         * @param resId the id of the string resource to match
         */
        fun withToolbarTitle(resId: Int): Matcher<View> {
            return object : BoundedMatcher<View, Toolbar>(Toolbar::class.java) {

                override fun describeTo(description: Description) {
                    description.appendText("with toolbar title from resource id: ")
                    description.appendValue(resId)
                }

                override fun matchesSafely(toolbar: Toolbar): Boolean {
                    var expectedText: CharSequence = ""
                    try {
                        expectedText = toolbar.resources.getString(resId)
                    } catch (ignored: Resources.NotFoundException) {
                        // view could be from a context unaware of the resource id.
                    }

                    return expectedText == toolbar.title
                }
            }
        }
    }
}