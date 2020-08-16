package oscar.riksdagskollen.screenshots;


import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.R;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar;
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
import static oscar.riksdagskollen.Utils.navigateToAndReapplyTheme;
import static oscar.riksdagskollen.Utils.performFling;
import static oscar.riksdagskollen.Utils.pressDeviceBackButton;
import static oscar.riksdagskollen.Utils.pressItemInRecyclerView;
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

    @BeforeClass
    public static void beforeAll() {
        CleanStatusBar.enableWithDefaults();
    }

    @Test
    public void testTakeScreenshot() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        // Fix colors being off for some reason by reapplying theme
        navigateToAndReapplyTheme(R.id.news_nav, activityRule);
        waitALittle();
        // Getting news screenshot
        Screengrab.screenshot("news");
        waitALittle();

        // Getting representative list screenshot
        navigateToAndReapplyTheme(R.id.rep_nav, activityRule);
        waitALittle();
        performFling(R.id.recycler_view);
        waitALittle();
        Screengrab.screenshot("representatives");

        // Getting representative screenshot
        pressItemInRecyclerView(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("representative");
        pressDeviceBackButton();

        // Getting party screenshot
        navigateToAndReapplyTheme(R.id.m_nav, activityRule);
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        waitALittle();
        Screengrab.screenshot("party");

        // Getting decision list screenshot
        navigateToAndReapplyTheme(R.id.dec_nav, activityRule);
        pressItemInRecyclerView(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("decisions");

        // Getting vote details screenshot
        navigateToAndReapplyTheme(R.id.votes_nav, activityRule);
        pressItemsInRecyclerViewWithDelay(R.id.recycler_view, 1);
        waitALittle();
        Screengrab.screenshot("vote");

        pressDeviceBackButton();

        // Getting a debate screenshot
        navigateToAndReapplyTheme(R.id.debate_nav, activityRule);

        // Check after a "Aktuell Debatt" item for a nice screenshot
        scrollView(R.id.recycler_view);
        for (int i = 0; i < 20; i++) {
            onView(withId(R.id.recycler_view))
                    .perform(actionOnItemAtPosition(i, scrollTo()));
            try {
                onView(withId(R.id.recycler_view))
                        .check(matches(atPosition(i, (hasDescendant(withText("Betänkande"))))));
                onView(withId(R.id.recycler_view))
                        .perform(actionOnItemAtPosition(i, click()));
                break;
            } catch (AssertionError e) {

            }
        }
        waitALittle();
        onView(withId(R.id.show_audio_player_header)).perform(click());
        waitALittle();
        Screengrab.screenshot("debate");
        pressDeviceBackButton();

    }

    @AfterClass
    public static void afterAll() {
        CleanStatusBar.disable();
    }


}
