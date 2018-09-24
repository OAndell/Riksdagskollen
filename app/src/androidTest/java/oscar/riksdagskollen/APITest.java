package oscar.riksdagskollen;

import android.test.suitebuilder.annotation.SmallTest;

import com.android.volley.VolleyError;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import oscar.riksdagskollen.Manager.RiksdagenAPIManager;
import oscar.riksdagskollen.Util.Callback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.Callback.ProtocolCallback;
import oscar.riksdagskollen.Util.Callback.VoteCallback;
import oscar.riksdagskollen.Util.JSONModel.CurrentNewsModels.CurrentNews;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.JSONModel.Vote;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by oscar on 2018-09-16.
 */


@SmallTest
public class APITest {

    private RiksdagenAPIManager apiManager;


    @Before
    public void setup(){
        RiksdagskollenApp app = RiksdagskollenApp.getInstance();
        apiManager = app.getRiksdagenAPIManager();
    }


    @Test
    public void currentNews_CorrectlyFetched_ReturnsTrue() throws InterruptedException {
        final Object syncObject = new Object();
        apiManager.getCurrentNews(new CurrentNewsCallback() {
                @Override
                public void onNewsFetched(List<CurrentNews> currentNews) {
                    assertTrue(currentNews.size() > 0);
                    for (int i = 0; i < currentNews.size(); i++) {
                        assertTrue(currentNews.get(i).getTitel().length() > 1);
                    }
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
    public void decisions_CorrectlyFetched_ReturnsTrue() throws InterruptedException {
        final Object syncObject = new Object();
        apiManager.getDecisions(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> decisions) {
                assertTrue(decisions.size() > 0);
                for (int i = 0; i < decisions.size(); i++) {
                    assertTrue(decisions.get(i).getTitel().length() > 1);
                }
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
    public void votes_CorrectlyFetched_ReturnsTrue() throws InterruptedException {
        final Object syncObject = new Object();
        apiManager.getVotes(new VoteCallback() {
            @Override
            public void onVotesFetched(List<Vote> votes) {
                assertTrue(votes.size() > 0);
                for (int i = 0; i < votes.size(); i++) {
                    assertTrue(votes.get(i).getTitel().length() > 1);
                }
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
    public void protocols_CorrectlyFetched_ReturnsTrue() throws InterruptedException {
        final Object syncObject = new Object();
        apiManager.getProtocols(new ProtocolCallback() {

            @Override
            public void onProtocolsFetched(List<Protocol> protocols) {
                assertTrue(protocols.size() > 0);
                for (int i = 0; i < protocols.size(); i++) {
                    assertTrue(protocols.get(i).getTitel().length() > 1);
                }
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






}