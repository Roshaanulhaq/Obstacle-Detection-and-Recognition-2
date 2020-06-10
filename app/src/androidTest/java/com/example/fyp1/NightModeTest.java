package com.example.fyp1;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class NightModeTest {
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity main = null;
    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);
    Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(DetectorActivity.class.getName(),null,false);
    @Before
    public void setUp() throws Exception {
        rule.launchActivity(MY_ACTIVITY_INTENT);
        main=rule.getActivity();
    }
    @Test
    public void testLaunch() throws Throwable {

        onView(withId(R.id.fragment_container)).perform(click());
        onView(withId(R.id.fragment_container)).perform(click());
        SystemClock.sleep(4000);
        Activity second=getInstrumentation().waitForMonitorWithTimeout(monitor,4000);
        assertNotNull(second);
    }
    @After
    public void tearDown() throws Exception {
    }
}