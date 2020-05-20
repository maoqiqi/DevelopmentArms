package com.codearms.maoqiqi.app.addedittask;

import android.content.Intent;
import android.content.res.Resources;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource;
import com.codearms.maoqiqi.app.data.source.room.TasksRoomDataSource;
import com.codearms.maoqiqi.app.tasks.TasksActivity;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;
import com.codearms.maoqiqi.app.utils.TestUtils;
import com.codearms.maoqiqi.app.utils.schedulers.ImmediateSchedulerProvider;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * Tests for the add task screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4.class)
public class AddEditTaskScreenTest {

    private static final String ID = "ID";
    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    private TaskBean taskBean = new TaskBean(ID, TITLE, DESCRIPTION, false);

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which init and releases Espresso Intents before and after each test run.
     */
    @Rule
    public ActivityTestRule<AddEditTaskActivity> addEditTaskActivityTestRule = new ActivityTestRule<>(AddEditTaskActivity.class, false, false);

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void addEmptyTask() {
        // Launch activity to add a new task
        launchNewTaskActivity(null);

        // Add invalid title and description combination
        onView(ViewMatchers.withId(R.id.editTextTitle)).perform(ViewActions.clearText());
        onView(ViewMatchers.withId(R.id.editTextDescription)).perform(ViewActions.clearText());

        // Try to save the task
        onView(ViewMatchers.withId(R.id.fabAddEditTask)).perform(ViewActions.click());

        // Verify that the activity is still displayed (a correct task would close it).
        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void rotation() {
        // Launch activity to add a new task
        launchNewTaskActivity(null);

        // Check that the toolbar shows the correct title
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.add_task)));

        // Rotate activity
        TestUtils.rotateOrientation(addEditTaskActivityTestRule.getActivity());

        // Check that the toolbar title is persisted
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.add_task)));
    }

    @Test
    public void editTask() {
        // Put a task in the repository and start the activity to edit it
        TasksRepository.destroyInstance();
        TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksRoomDataSource.getInstance(InstrumentationRegistry.getTargetContext(), new ImmediateSchedulerProvider())).addTask(taskBean);
        launchNewTaskActivity(taskBean.getId());

        // Check that the toolbar shows the correct title
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.edit_task)));

        // Rotate activity
        TestUtils.rotateOrientation(addEditTaskActivityTestRule.getActivity());

        // Check that the toolbar title is persisted
        onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(withToolbarTitle(R.string.edit_task)));

        onView(ViewMatchers.withId(R.id.editTextTitle)).check(ViewAssertions.matches(ViewMatchers.withText(taskBean.getTitle())));
        onView(ViewMatchers.withId(R.id.editTextDescription)).check(ViewAssertions.matches(ViewMatchers.withText(taskBean.getDescription())));
    }

    /**
     * Launch AddEditTaskActivity
     *
     * @param taskId is null if used to add a new task, otherwise it edits the task.
     */
    private void launchNewTaskActivity(String taskId) {
        Intent intent = new Intent();
        intent.putExtra(TasksActivity.EXTRA_TASK_ID, taskId);
        addEditTaskActivityTestRule.launchActivity(intent);
    }

    /**
     * Matches the toolbar title with a specific string resource.
     *
     * @param resId the id of the string resource to match
     */
    public static Matcher<View> withToolbarTitle(final int resId) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title from resource id: ");
                description.appendValue(resId);
            }

            @Override
            protected boolean matchesSafely(Toolbar toolbar) {
                CharSequence expectedText = "";
                try {
                    expectedText = toolbar.getResources().getString(resId);
                } catch (Resources.NotFoundException ignored) {
                    // view could be from a context unaware of the resource id.
                }
                return expectedText.equals(toolbar.getTitle());
            }
        };
    }
}