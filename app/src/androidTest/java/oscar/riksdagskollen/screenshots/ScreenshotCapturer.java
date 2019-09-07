package oscar.riksdagskollen.screenshots;


import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.R;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;
import tools.fastlane.screengrab.locale.LocaleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static oscar.riksdagskollen.Utils.atPosition;
import static oscar.riksdagskollen.Utils.navigateTo;
import static oscar.riksdagskollen.Utils.pressDeviceBackButton;
import static oscar.riksdagskollen.Utils.pressItemsInRecyclerViewWithDelay;
import static oscar.riksdagskollen.Utils.scrollView;
import static oscar.riksdagskollen.Utils.waitALittle;

@RunWith(JUnit4.class)
public class ScreenshotCapturer {


    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_CONFIGURATION);

    @Before
    public void setLocale() {
        LocaleUtil.changeDeviceLocaleTo(LocaleUtil.getTestLocale());
    }

    @Test
    public void testTakeScreenshot() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        navigateTo(R.id.about_nav);
        navigateTo(R.id.news_nav);
        waitALittle();
        // Getting news screenshot
        Screengrab.screenshot("news");

        // Getting a debate screenshot
        navigateTo(R.id.debate_nav);

        // Check after a "Aktuell Debatt" item for a nice screenshot
        scrollView(R.id.recycler_view);
        for (int i = 0; i < 20; i++) {
            onView(withId(R.id.recycler_view))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            try {
                onView(withId(R.id.recycler_view))
                        .check(matches(atPosition(i, hasDescendant(withText("Aktuell Debatt")))));
                onView(withId(R.id.recycler_view))
                        .perform(actionOnItemAtPosition(i, click()));
                break;
            } catch (AssertionError e) {

            }
        }
        waitALittle();
        Screengrab.screenshot("debate");
        pressDeviceBackButton();

        // Getting representative list screenshot
        navigateTo(R.id.rep_nav);
        waitALittle();
        Screengrab.screenshot("representatives");

        // Getting representative screenshot
        pressItemsInRecyclerViewWithDelay(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("representative");
        pressDeviceBackButton();

        // Getting party screenshot
        navigateTo(R.id.m_nav);
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        waitALittle();
        Screengrab.screenshot("party");

        // Getting decision list screenshot
        navigateTo(R.id.dec_nav);
        pressItemsInRecyclerViewWithDelay(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("decisions");

        // Getting vote details screenshot
        navigateTo(R.id.votes_nav);
        pressItemsInRecyclerViewWithDelay(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("vote");

    }


}
