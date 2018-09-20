package oscar.riksdagskollen;

import android.os.Debug;
import android.os.Parcel;
import android.support.test.annotation.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oscar on 2018-09-16.
 * TODO: NOT WORKING AT ALL
 */
import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Util.Callback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.Callback.VoteCallback;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;


// @RunWith is required only if you use a mix of JUnit3 and JUnit4.
@RunWith(AndroidJUnit4.class)
@SmallTest
public class APITest { //

    private RiksdagenAPIManager apiManager;


    @Before
    public void setup(){
        RikdagskollenApp app = RikdagskollenApp.getInstance();
        apiManager = app.getRiksdagenAPIManager();
    }

    @Test
    @UiThreadTest
    public void currentNews_CorrectlyFetched_ReturnsTrue() throws InterruptedException {
        final Object syncObject = new Object();
        apiManager.getCurrentNews(new CurrentNewsCallback() {
                @Override
                public void onNewsFetched(List<CurrentNews> currentNews) {
                    assertTrue(currentNews.size() > 0);
                    synchronized (syncObject){
                        syncObject.notify();
                    }
                }
                @Override
                public void onFail(VolleyError error) {
                    fail(error.getMessage());
                    synchronized (syncObject){
                        syncObject.notify();
                    }
                }
            },1);
        synchronized (syncObject){
            syncObject.wait();
        }
    }

    @Test
    @UiThreadTest
    public void decisions_CorrectlyFetched_ReturnsTrue() {
        for (int pageNumber = 1; pageNumber < 3; pageNumber++) {
            apiManager.getDecisions(new DecisionsCallback() {
                @Override
                public void onDecisionsFetched(List<DecisionDocument> decisions) {
                    assertTrue(decisions.size()>0);
                }

                @Override
                public void onFail(VolleyError error) {
                    fail(error.getMessage());
                }
            },pageNumber);
        }
    }


    @Test
    @UiThreadTest
    public void votes_CorrectlyFetched_ReturnsTrue() {
        for (int pageNumber = 1; pageNumber < 3; pageNumber++) {
            apiManager.getVotes(new VoteCallback() {
                @Override
                public void onVotesFetched(List<Vote> votes) {
                    assertTrue(votes.size() > 0);

                    assertTrue(false);

                }

                @Override
                public void onFail(VolleyError error) {
                    fail(error.getMessage());
                }
            },pageNumber);
        }}

}