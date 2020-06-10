package com.example.fyp1;

import android.app.Activity;
import android.app.Instrumentation;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.*;

public class LoginTest2 {
    @Rule
    public ActivityTestRule<Login> rule = new ActivityTestRule<Login>(Login.class);
    private Login login = null;
    Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(MainActivity.class.getName(),null,false);
    @Before
    public void setUp() throws Exception {
        login = rule.getActivity();
    }
    @Test
    public void testLaunch() throws Throwable {

        assertNotNull(login.findViewById(R.id.button));
        EditText pw=login.findViewById(R.id.editText);
        EditText username=login.findViewById(R.id.editText2);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                pw.setText("12345678");
                username.setText("muneeb@gmail.com");
            }
        });


        onView(withId(R.id.button)).perform(click());

        Activity second=getInstrumentation().waitForMonitorWithTimeout(monitor,5000);
        assertNotNull(second);



    }
    @After
    public void tearDown() throws Exception {
        rule=null;
    }
}