package com.ioanapascu.edfocus;

import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.MenuItem;

import com.ioanapascu.edfocus.shared.DrawerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Ioana Pascu on 6/10/2018.
 */
public class DrawerActivityTest {

    @Rule
    public ActivityTestRule<DrawerActivity> mActivityTestRule = new ActivityTestRule<DrawerActivity>(DrawerActivity.class) {

    };

    private DrawerActivity mDrawerActivity = null;

    @Before
    public void setUp() throws Exception {
        mDrawerActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mDrawerActivity = null;
    }

    @Test
    public void displayTest() throws Exception {
        //given(mDrawerActivity.getCurrentUserType()).willReturn("teacher");
        //doReturn("teacher").when(drawerActivity).getCurrentUserType();
        //doNothing().when(mDrawerActivity).markUserOnline();

        //onView(withId(R.id.button_play_pause_toggle)).perform(click());
        //mDrawerActivity.setCURRENT_USER_ID("teacher");
        NavigationView navigationView = mDrawerActivity.findViewById(R.id.nav_view);
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_classes);

        assertNotNull(item);
        Log.e("~~", String.valueOf(item.isVisible()));
        //assertEquals(item.isVisible(), true);
        //assertThat(item, new VisibilityMatcher(VISIBLE));
        assertTrue(item.isVisible());
        //Mockito.doReturn(true).when(mDrawerActivity).ge();
    }

}