package com.codearms.maoqiqi.app.tasks

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.core.util.Preconditions
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.codearms.maoqiqi.app.Injection
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.data.source.TasksDataSource
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource
import com.codearms.maoqiqi.app.utils.TestUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the tasks screen, the main screen which contains a list of all tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4::class)
class TasksScreenTest {

    /**
     * [ActivityTestRule] is a JUnit [@Rule][Rule] to launch your activity under test.
     */
    @get:Rule
    var tasksActivityTestRule: ActivityTestRule<TasksActivity> = object : ActivityTestRule<TasksActivity>(TasksActivity::class.java) {

        /**
         * To avoid a long list of tasks and the need to scroll through the list to find a
         * task, we call [TasksDataSource.deleteAllTasks] before each test.
         */
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            // Doing this in @Before generates a race condition.
            Injection.provideTasksRepository(InstrumentationRegistry.getTargetContext()).deleteAllTasks()
        }
    }

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with  Espresso.
     * IdlingResource resource is a great way to tell Espresso when your app is in an  idle state.
     * This helps Espresso to synchronize your test actions, which makes tests significantly more reliable.
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addTask() {
        // Click on the add task button
        onView(ViewMatchers.withId(R.id.fabAddTask)).perform(ViewActions.click())
        // Check if the add task screen is displayed
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Add task title and description
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.typeText(TITLE), ViewActions.closeSoftKeyboard())
        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.typeText(DESCRIPTION), ViewActions.closeSoftKeyboard())

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click())
    }

    @Test
    fun editTask() {
        // First add a task
        addTask()

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click())

        // Click on the edit task button
        onView(ViewMatchers.withId(R.id.fabEditTask)).perform(ViewActions.click())

        // Edit task title and description
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_TITLE), ViewActions.closeSoftKeyboard())
        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.replaceText(NEW_DESCRIPTION), ViewActions.closeSoftKeyboard())

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click())

        // Verify task is displayed on screen in the task list.
        onView(withItemText(NEW_TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // Verify previous task is not displayed
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun markTaskAsComplete() {
        // Add active task
        addTask()

        // Mark the task as complete
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // Verify task is shown as complete
        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click())
        // onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        // complete tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.completed)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun markTaskAsActive() {
        // Add completed task
        addTask()
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // Mark the task as active
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // Verify task is shown as active
        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // complete tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.completed)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun deleteTask() {
        // Add a active task
        addTask()

        // Open it in details view
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click())

        // Click delete task in menu
        onView(ViewMatchers.withId(R.id.menu_delete)).perform(ViewActions.click())

        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click())

        // Verify it was deleted
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun clearCompletedTasks() {
        // Add a complete task
        addTask()
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // Click clear completed in menu
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext())
        // Notice:需要关闭"动画程序时长缩放"
        onView(ViewMatchers.withText(R.string.clear_completed)).perform(ViewActions.click())

        // Verify that completed tasks are not shown
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun markTaskAsCompleteOnDetail() {
        // Add a active task
        addTask()

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click())

        // Click on the checkbox in task details screen
        onView(ViewMatchers.withId(R.id.cbComplete)).perform(ViewActions.click())

        // Click on the navigation up button to go back to the list
        onView(ViewMatchers.withContentDescription(getToolbarNavigationContentDescription())).perform(ViewActions.click())

        // Check that the task is marked as completed
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE))))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun markTaskAsActiveOnDetailScreen() {
        // Add a completed task
        addTask()
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click())

        // Click on the checkbox in task details screen
        onView(ViewMatchers.withId(R.id.cbComplete)).perform(ViewActions.click())

        // Click on the navigation up button to go back to the list
        onView(ViewMatchers.withContentDescription(getToolbarNavigationContentDescription())).perform(ViewActions.click())

        // Check that the task is marked as active
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE))))
                .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
    }

    @Test
    fun orientationChangeSaveFilterType() {
        // Add a completed task
        addTask()
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click())

        // when switching to active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click())

        // then no tasks should appear
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        // when rotating the screen
        TestUtils.rotateOrientation(tasksActivityTestRule.activity)

        // then nothing changes
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist())

        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click())
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun orientationChangeSaveContent() {
        // Add a completed task
        addTask()

        // Open the task in details view
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click())

        // Click on the edit task button
        onView(ViewMatchers.withId(R.id.fabEditTask)).perform(ViewActions.click())

        // Change task title (but don't save)
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_TITLE), ViewActions.closeSoftKeyboard())

        // Rotate the screen
        TestUtils.rotateOrientation(TestUtils.getCurrentActivity())

        // Verify task title is restored
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.withText(NEW_TITLE)))

        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_DESCRIPTION), ViewActions.closeSoftKeyboard())

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click())

        // Verify task is displayed on screen in the task list.
        onView(withItemText(NEW_TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // Verify previous task is not displayed
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist())
    }

    private fun withItemText(itemText: String): Matcher<View> {
        Preconditions.checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty")
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {
                return Matchers.allOf(
                        ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(androidx.recyclerview.widget.RecyclerView::class.java)),
                        ViewMatchers.withText(itemText)).matches(item)
            }

            override fun describeTo(description: Description) {
                description.appendText("is isDescendantOfA LV with text $itemText")
            }
        }
    }

    private fun getToolbarNavigationContentDescription(): String {
        return TestUtils.getToolbarNavigationContentDescription(tasksActivityTestRule.activity, R.id.toolbar)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val DESCRIPTION = "DESCRIPTION"

        private const val NEW_TITLE = "NEW_TITLE"
        private const val NEW_DESCRIPTION = "NEW_DESCRIPTION"
    }
}