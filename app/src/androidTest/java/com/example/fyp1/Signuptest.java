package com.example.fyp1;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.*;

public class Signuptest {

    public ActivityTestRule<Main2Activity> rule = new ActivityTestRule<Main2Activity>(Main2Activity.class);
    private Main2Activity signup = null;
    Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(Login.class.getName(),null,false);
    private static final Intent MY_ACTIVITY_INTENT = new Intent(InstrumentationRegistry.getTargetContext(), Main2Activity.class);
    @Before
    public void setUp() throws Exception {
        rule.launchActivity(MY_ACTIVITY_INTENT);
        signup = rule.getActivity();
    }
    @Test
    public void checksignup () throws Throwable {
        assertNotNull(signup.findViewById(R.id.button));
        EditText email=signup.findViewById(R.id.email);
        EditText username=signup.findViewById(R.id.editText2);
        EditText pw = signup.findViewById(R.id.editText);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                pw.setText("12345678");
                username.setText("roshaan1");
                email.setText("roshan123@gmail.com");
            }
        });
        onView(withId(R.id.button)).perform(click());
        Activity second=getInstrumentation().waitForMonitorWithTimeout(monitor,6000);
        assertNotNull(second);
    }
    @After
    public void tearDown() throws Exception {
    }
}