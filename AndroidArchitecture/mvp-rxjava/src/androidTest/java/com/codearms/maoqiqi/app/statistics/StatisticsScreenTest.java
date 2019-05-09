package com.codearms.maoqiqi.app.statistics;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.codearms.maoqiqi.app.data.TaskBean;
import com.codearms.maoqiqi.app.data.source.TasksRepository;
import com.codearms.maoqiqi.app.data.source.remote.TasksRemoteDataSource;
import com.codearms.maoqiqi.app.data.source.room.TasksRoomDataSource;
import com.codearms.maoqiqi.app.utils.EspressoIdlingResource;
import com.codearms.maoqiqi.app.utils.schedulers.ImmediateSchedulerProvider;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;

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
        TasksRepository repository = TasksRepository.getInstance(TasksRemoteDataSource.getInstance(), TasksRoomDataSource.getInstance(InstrumentationRegistry.getTargetContext(), new ImmediateSchedulerProvider()));
        repository.deleteAllTasks();
        repository.addTask(new TaskBean(TITLE, DESCRIPTION, false));
        repository.addTask(new TaskBean(TITLE, DESCRIPTION, false));
        repository.addTask(new TaskBean(TITLE, DESCRIPTION, true));

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