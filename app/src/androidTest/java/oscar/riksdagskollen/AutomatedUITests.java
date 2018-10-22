package oscar.riksdagskollen;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import oscar.riksdagskollen.Activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AutomatedUITests {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Test
    public void test_News() {
        System.out.println("Testing news");
        navigateTo(R.id.news_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_Votes() {
        System.out.println("Testing votes");
        navigateTo(R.id.votes_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_Decisions() {
        System.out.println("Testing decisions");
        navigateTo(R.id.dec_nav);
        scrollView(R.id.recycler_view);
        expandCollapseItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_Reps() {
        System.out.println("Testing reps");
        navigateTo(R.id.rep_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_Protocols() {
        System.out.println("Testing protocols");
        navigateTo(R.id.prot_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_S() {
        System.out.println("Testing S");
        navigateTo(R.id.s_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_M() {
        System.out.println("Testing m");
        navigateTo(R.id.m_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_SD() {
        System.out.println("Testing sd");
        navigateTo(R.id.sd_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_C() {
        System.out.println("Testing C");
        navigateTo(R.id.c_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_V() {
        System.out.println("Testing V");
        navigateTo(R.id.v_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_KD() {
        System.out.println("Testing kd");
        navigateTo(R.id.kd_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_L() {
        System.out.println("Testing L");
        navigateTo(R.id.l_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }

    @Test
    public void test_MP() {
        System.out.println("Testing mp");
        navigateTo(R.id.mp_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view);
    }


    @Test
    public void test_About() {
        System.out.println("Testing about");
        navigateTo(R.id.about_nav);
    }

    private void navigateTo(int menuId) {
        System.out.println("Opening drawer");
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(menuId));
    }


    private void scrollView(int viewId) {
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
    }

    private void pressItemsInRecyclerView(int recyclerViewId) {
        System.out.println("Pressing items in view");
        for (int i = 0; i < 20; i++) {
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, click()));
            System.out.println("Sleeping");
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pressBack();
        }
    }

    private void expandCollapseItemsInRecyclerView(int recyclerViewId) {
        System.out.println("Pressing items in view");
        for (int i = 0; i < 20; i++) {
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, click()));
            System.out.println("Sleeping");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, click()));
        }
    }

    private void pressBack() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
    }
}
