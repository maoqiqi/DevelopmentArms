package com.codearms.maoqiqi.app.tasks;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.data.source.TasksDataSource;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;
import com.codearms.maoqiqi.app.utils.TestUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * Tests for the tasks screen, the main screen which contains a list of all tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4.class)
public class TasksScreenTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    private final static String NEW_TITLE = "NEW_TITLE";
    private final static String NEW_DESCRIPTION = "NEW_DESCRIPTION";

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     */
    @Rule
    public ActivityTestRule<TasksActivity> tasksActivityTestRule = new ActivityTestRule<TasksActivity>(TasksActivity.class) {

        /**
         * To avoid a long list of tasks and the need to scroll through the list to find a
         * task, we call {@link TasksDataSource#deleteAllTasks()} before each test.
         */
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            // Doing this in @Before generates a race condition.
            Injection.provideTasksRepository(InstrumentationRegistry.getTargetContext()).deleteAllTasks();
        }
    };

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with  Espresso.
     * IdlingResource resource is a great way to tell Espresso when your app is in an  idle state.
     * This helps Espresso to synchronize your test actions, which makes tests significantly more reliable.
     */
    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void addTask() {
        // Click on the add task button
        onView(ViewMatchers.withId(R.id.fabAddTask)).perform(ViewActions.click());
        // Check if the add task screen is displayed
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Add task title and description
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.typeText(TITLE), ViewActions.closeSoftKeyboard());
        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.typeText(DESCRIPTION), ViewActions.closeSoftKeyboard());

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click());
    }

    @Test
    public void editTask() {
        // First add a task
        addTask();

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click());

        // Click on the edit task button
        onView(ViewMatchers.withId(R.id.fabEditTask)).perform(ViewActions.click());

        // Edit task title and description
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_TITLE), ViewActions.closeSoftKeyboard());
        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.replaceText(NEW_DESCRIPTION), ViewActions.closeSoftKeyboard());

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click());

        // Verify task is displayed on screen in the task list.
        onView(withItemText(NEW_TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        // Verify previous task is not displayed
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void markTaskAsComplete() {
        // Add active task
        addTask();

        // Mark the task as complete
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // Verify task is shown as complete
        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click());
        // onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));

        // complete tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.completed)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void markTaskAsActive() {
        // Add completed task
        addTask();
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // Mark the task as active
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // Verify task is shown as active
        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // complete tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.completed)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));
    }

    @Test
    public void deleteTask() {
        // Add a active task
        addTask();

        // Open it in details view
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click());

        // Click delete task in menu
        onView(ViewMatchers.withId(R.id.menu_delete)).perform(ViewActions.click());

        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click());

        // Verify it was deleted
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));
    }

    @Test
    public void clearCompletedTasks() {
        // Add a complete task
        addTask();
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // Click clear completed in menu
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        // Notice:需要关闭"动画程序时长缩放"
        onView(ViewMatchers.withText(R.string.clear_completed)).perform(ViewActions.click());

        // Verify that completed tasks are not shown
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));
    }

    @Test
    public void markTaskAsCompleteOnDetail() {
        // Add a active task
        addTask();

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click());

        // Click on the checkbox in task details screen
        onView(ViewMatchers.withId(R.id.cbComplete)).perform(ViewActions.click());

        // Click on the navigation up button to go back to the list
        onView(ViewMatchers.withContentDescription(getToolbarNavigationContentDescription())).perform(ViewActions.click());

        // Check that the task is marked as completed
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE))))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()));
    }

    @Test
    public void markTaskAsActiveOnDetailScreen() {
        // Add a completed task
        addTask();
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // Click on the task on the list
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click());

        // Click on the checkbox in task details screen
        onView(ViewMatchers.withId(R.id.cbComplete)).perform(ViewActions.click());

        // Click on the navigation up button to go back to the list
        onView(ViewMatchers.withContentDescription(getToolbarNavigationContentDescription())).perform(ViewActions.click());

        // Check that the task is marked as active
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE))))
                .check(ViewAssertions.matches(ViewMatchers.isNotChecked()));
    }

    @Test
    public void orientationChangeSaveFilterType() {
        // Add a completed task
        addTask();
        onView(Matchers.allOf(ViewMatchers.withId(R.id.cbComplete), ViewMatchers.hasSibling(ViewMatchers.withText(TITLE)))).perform(ViewActions.click());

        // when switching to active tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.active)).perform(ViewActions.click());

        // then no tasks should appear
        onView(withItemText(TITLE)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));

        // when rotating the screen
        TestUtils.rotateOrientation(tasksActivityTestRule.getActivity());

        // then nothing changes
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist());

        // all tasks
        onView(ViewMatchers.withId(R.id.menu_filter)).perform(ViewActions.click());
        onView(ViewMatchers.withText(R.string.all)).perform(ViewActions.click());
        onView(withItemText(TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void orientationChangeSaveContent() {
        // Add a completed task
        addTask();

        // Open the task in details view
        onView(ViewMatchers.withText(TITLE)).perform(ViewActions.click());

        // Click on the edit task button
        onView(ViewMatchers.withId(R.id.fabEditTask)).perform(ViewActions.click());

        // Change task title (but don't save)
        // Type new task title
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_TITLE), ViewActions.closeSoftKeyboard());

        // Rotate the screen
        TestUtils.rotateOrientation(TestUtils.getCurrentActivity());

        // Verify task title is restored
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.withText(NEW_TITLE)));

        // Type new task description and close the keyboard
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.replaceText(""), ViewActions.typeText(NEW_DESCRIPTION), ViewActions.closeSoftKeyboard());

        // Save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click());

        // Verify task is displayed on screen in the task list.
        onView(withItemText(NEW_TITLE)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        // Verify previous task is not displayed
        onView(withItemText(TITLE)).check(ViewAssertions.doesNotExist());
    }

    private Matcher<View> withItemText(final String itemText) {
        Preconditions.checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return Matchers.allOf(
                        ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(RecyclerView.class)),
                        ViewMatchers.withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA LV with text " + itemText);
            }
        };
    }

    private String getToolbarNavigationContentDescription() {
        return TestUtils.getToolbarNavigationContentDescription(tasksActivityTestRule.getActivity(), R.id.toolbar);
    }
}