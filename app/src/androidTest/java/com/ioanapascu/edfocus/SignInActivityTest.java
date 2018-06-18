package com.ioanapascu.edfocus;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Ioana Pascu on 6/10/2018.
 */
public class SignInActivityTest {

    private static final String EMAIL = "scorppion2005@yahoo.com";
    private static final String CORRECT_PASS = "123456";
    private static final String WRONG_PASS = "passme123";
    private static final String msg = "The password is invalid or the user does not have a password.";
    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);
    SignInActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void test() {
        onView((withId(R.id.edit_text_email)))
                .perform(ViewActions.typeText(EMAIL));
        onView(withId(R.id.edit_text_password))
                .perform(ViewActions.typeText(WRONG_PASS));

        onView(withId(R.id.button_sign_in))
                .perform(ViewActions.click());

        onView(withText(msg)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void test2() {
        onView((withId(R.id.edit_text_email)))
                .perform(ViewActions.typeText(EMAIL));

        onView(withId(R.id.edit_text_password))
                .perform(ViewActions.typeText(WRONG_PASS));

        onView(withId(R.id.button_sign_in))
                .perform(ViewActions.click());

        //intended(hasComponent(DrawerActivity.class.getName()));
    }


}