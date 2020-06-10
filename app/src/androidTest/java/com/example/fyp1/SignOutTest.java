package com.example.fyp1;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import androidx.fragment.app.Fragment;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class SignOutTest {
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity main = null;
    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), MainActivity.class);
    Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(Login.class.getName(),null,false);
    @Before
    public void setUp() throws Exception {
        rule.launchActivity(MY_ACTIVITY_INTENT);
        main=rule.getActivity();

    }

    @Test
    public void testsignout() throws Exception{

        onView(withId(R.id.fragment_container)).perform(swipeRight());
        Activity second=getInstrumentation().waitForMonitorWithTimeout(monitor,6000);
        assertNotNull(second);
    }
    @After
    public void tearDown() throws Exception {
    }
}