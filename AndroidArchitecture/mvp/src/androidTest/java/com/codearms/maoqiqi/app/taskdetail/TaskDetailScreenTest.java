package com.codearms.maoqiqi.app.taskdetail;

import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource;
import com.codearms.maoqiqi.app.tasks.TasksActivity;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;
import com.codearms.maoqiqi.app.utils.TestUtils;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;

/**
 * Tests for the task detail screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4.class)
public class TaskDetailScreenTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    private static TaskBean ACTIVE_TASK = new TaskBean(TITLE, DESCRIPTION, false);
    private static TaskBean COMPLETED_TASK = new TaskBean(TITLE, DESCRIPTION, true);

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     */
    @Rule
    public ActivityTestRule<TaskDetailActivity> taskDetailActivityTestRule = new ActivityTestRule<>(TaskDetailActivity.class, true, false);

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void loadActiveTask() {
        startActivity(ACTIVE_TASK);

        // Check that the task title and description are displayed
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)));
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)));
        onView(ViewMatchers.withId(R.id.cbComplete)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isChecked())));
    }

    @Test
    public void loadCompletedTask() {
        startActivity(COMPLETED_TASK);

        // Check that the task title and description are displayed
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)));
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)));
        onView(ViewMatchers.withId(R.id.cbComplete)).check(ViewAssertions.matches(ViewMatchers.isChecked()));
    }

    @Test
    public void rotation() {
        loadActiveTask();

        // Check delete menu item is displayed and is unique
        onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Rotate activity
        TestUtils.rotateOrientation(taskDetailActivityTestRule.getActivity());

        // Check that the task is shown
        onView(ViewMatchers.withId(R.id.tvTitle)).check(ViewAssertions.matches(ViewMatchers.withText(TITLE)));
        onView(ViewMatchers.withId(R.id.tvDescription)).check(ViewAssertions.matches(ViewMatchers.withText(DESCRIPTION)));

        // Check delete menu item is displayed and is unique
        onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void startActivity(TaskBean taskBean) {
        TasksRepository.destroyInstance();
        TasksRemoteDataSource.getInstance().addTask(taskBean);

        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(TasksActivity.EXTRA_TASK_ID, taskBean.getId());
        taskDetailActivityTestRule.launchActivity(startIntent);
    }
}