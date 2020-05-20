package com.codearms.maoqiqi.app.statistics;

import android.content.Intent;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * Tests for the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/15 10:00
 */
@RunWith(AndroidJUnit4.class)
public class StatisticsScreenTest {

    private final static String TITLE = "TITLE";
    private final static String DESCRIPTION = "DESCRIPTION";

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     */
    @Rule
    public ActivityTestRule<StatisticsActivity> statisticsActivityTestRule = new ActivityTestRule<>(StatisticsActivity.class, true, false);

    @Before
    public void setUp() {
        // Given some tasks
        Injection.provideTasksRepository(InstrumentationRegistry.getTargetContext()).deleteAllTasks();
        TasksRemoteDataSource.getInstance().addTask(new TaskBean(TITLE, DESCRIPTION, false));
        TasksRemoteDataSource.getInstance().addTask(new TaskBean(TITLE, DESCRIPTION, false));
        TasksRemoteDataSource.getInstance().addTask(new TaskBean(TITLE, DESCRIPTION, true));

        // Lazily start the Activity from the ActivityTestRule
        Intent intent = new Intent();
        statisticsActivityTestRule.launchActivity(intent);
    }

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void showsMessage() {
        // Check that the active and completed tasks text is displayed
        onView(ViewMatchers.withText(Matchers.containsString("未完成任务总数：2"))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withText(Matchers.containsString("已完成任务总数：1"))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}