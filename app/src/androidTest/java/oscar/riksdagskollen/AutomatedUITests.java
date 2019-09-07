package oscar.riksdagskollen;


import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import oscar.riksdagskollen.Activity.MainActivity;

import static oscar.riksdagskollen.Utils.expandCollapseItemsInRecyclerView;
import static oscar.riksdagskollen.Utils.navigateTo;
import static oscar.riksdagskollen.Utils.pressItemsInRecyclerView;
import static oscar.riksdagskollen.Utils.scrollView;

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
        pressItemsInRecyclerView(R.id.recycler_view, 25);
    }

    @Test
    public void test_Votes() {
        System.out.println("Testing votes");
        navigateTo(R.id.votes_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 60);
    }

    @Test
    public void test_Decisions() {
        System.out.println("Testing decisions");
        navigateTo(R.id.dec_nav);
        scrollView(R.id.recycler_view);
        expandCollapseItemsInRecyclerView(R.id.recycler_view, 40);
    }

    @Test
    public void test_Representatives() {
        System.out.println("Testing reps");
        navigateTo(R.id.rep_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 60);
    }

    @Test
    public void test_Protocols() {
        System.out.println("Testing protocols");
        navigateTo(R.id.prot_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_S() {
        System.out.println("Testing S");
        navigateTo(R.id.s_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_M() {
        System.out.println("Testing m");
        navigateTo(R.id.m_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_SD() {
        System.out.println("Testing sd");
        navigateTo(R.id.sd_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_C() {
        System.out.println("Testing C");
        navigateTo(R.id.c_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_V() {
        System.out.println("Testing V");
        navigateTo(R.id.v_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_KD() {
        System.out.println("Testing kd");
        navigateTo(R.id.kd_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_L() {
        System.out.println("Testing L");
        navigateTo(R.id.l_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }

    @Test
    public void test_MP() {
        System.out.println("Testing mp");
        navigateTo(R.id.mp_nav);
        scrollView(R.id.recycler_view);
        pressItemsInRecyclerView(R.id.recycler_view, 20);
    }


    @Test
    public void test_About() {
        System.out.println("Testing about");
        navigateTo(R.id.about_nav);
    }

}
