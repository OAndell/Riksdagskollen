package oscar.riksdagskollen;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class Utils {

    public static void pressDeviceBackButton() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
    }

    // Test click to expand and then collapse the 40 first items in the recyclerview defined
    public static void expandCollapseItemsInRecyclerView(int recyclerViewId, int numberOfItemsToTest) {
        System.out.println("Pressing items in view");
        for (int i = 0; i < numberOfItemsToTest; i++) {
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


    public static void pressItemsInRecyclerView(int recyclerViewId, int numberOfItemsToTest) {
        System.out.println("Pressing items in view");
        for (int i = 0; i < numberOfItemsToTest; i++) {
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, click()));
            System.out.println("Sleeping");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pressDeviceBackButton();
        }
    }

    public static void pressItemsInRecyclerViewWithDelay(int recyclerViewId, int numberOfItemsToTest) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < numberOfItemsToTest; i++) {
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            onView(withId(recyclerViewId))
                    .perform(actionOnItemAtPosition(i, click()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pressItemInRecyclerView(int recyclerViewId, int index) {
        onView(withId(recyclerViewId))
                .perform(actionOnItemAtPosition(index, click()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void performFling(int viewId) {
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
        onView(withId(viewId)).perform(swipeUp());
    }

    public static void scrollView(int viewId) {
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

    public static void navigateTo(int menuId) {
        System.out.println("Opening drawer");
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(menuId));
    }

    public static void navigateToAndReapplyTheme(int menuId, ActivityTestRule activityTestRule) {
        navigateTo(menuId);
        reApplyTheme(activityTestRule);
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static void reApplyTheme(ActivityTestRule rule) {
        rule.getActivity().runOnUiThread(() -> {
            rule.getActivity().getApplication().setTheme(R.style.DefaultTheme);
            rule.getActivity().recreate();
        });
    }

    public static void waitALittle() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
