package oscar.riksdagskollen.Util.Job;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.VolleyError;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import oscar.riksdagskollen.CurrentNews.CurrentNewsCallback;
import oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels.CurrentNews;
import oscar.riksdagskollen.CurrentNews.CurrentNewsListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.NotificationHelper;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class CheckAlertsJob extends Job {

    public static final String TAG = "job_alert";

    private CountDownLatch countDownLatch;


    public static void scheduleJob() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RiksdagskollenApp.getInstance());

        int updateFreq = Integer.valueOf(sharedPreferences.getString("update_freq", "540"));
        JobRequest.NetworkType networkType = sharedPreferences.getBoolean("only_wifi", false) ? JobRequest.NetworkType.UNMETERED : JobRequest.NetworkType.CONNECTED;

        Log.d(TAG, "Scheduled alert with update frequency " + updateFreq + " and network type " + networkType);

        new JobRequest.Builder(CheckAlertsJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(updateFreq))
                .setRequiredNetworkType(networkType)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // Cancel job if no alerts are active
        AlertManager manager = AlertManager.getInstance();
        int alerts = manager.getAlertCount();
        Log.d(TAG, "onRunJob: alert count: " + alerts);
        //writeToFile("Alert check job run at: " + Calendar.getInstance().getTime().toString() + ". \n");

        if (alerts == 0) {
            cancel();
            return Result.SUCCESS;
        }

        countDownLatch = new CountDownLatch(alerts);

        for (String dokId : manager.getReplyAlerts()) {
            getDocument(dokId, countDownLatch);
        }

        for (String partyId : manager.getPartyAlerts().keySet()) {
            checkPartyForNewDocument(partyId, manager.getPartyAlerts().get(partyId), countDownLatch);
        }

        for (String section : manager.getSectionAlerts().keySet()) {
            checkSectionForNewDocument(section, manager.getSectionAlerts().get(section), countDownLatch);
        }

        try {
            //Do not wait longer than 30 seconds
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }

        return Result.SUCCESS;
    }

    // Check for reply document trough documentid

    private void checkPartyForNewDocument(final String partyId, final String latestDocId, final CountDownLatch countDownLatch) {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(partyId, 1, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                if (!documents.get(0).getId().equals(latestDocId)) {
                    //showMonitorNotification(partyId, true, null);
                    NotificationHelper.showPartyNotification(partyId, getContext());
                    RiksdagskollenApp.getInstance()
                            .getAlertManager()
                            .setAlertEnabledForPartyDocuments(partyId, documents.get(0).getId(), true);
                    countDownLatch.countDown();
                } else {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onFail(VolleyError error) {
                countDownLatch.countDown();
            }
        });
    }

    private void checkSectionForNewDocument(final String section, final String latestDocID, final CountDownLatch countDownLatch) {
        switch (section) {
            case VoteListFragment.SECTION_NAME_VOTE:
                RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getVotes(new VoteCallback() {
                    @Override
                    public void onVotesFetched(List<Vote> votes) {
                        if (!votes.get(0).getId().equals(latestDocID)) {
                            NotificationHelper.showVoteNotification(votes.get(0), getContext());
                            RiksdagskollenApp.getInstance()
                                    .getAlertManager()
                                    .setAlertEnabledForSection(section, votes.get(0).getId(), true);
                        }
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFail(VolleyError error) {
                        countDownLatch.countDown();
                    }
                }, 1);
                break;
            case CurrentNewsListFragment.SECTION_NAME_NEWS:
                RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(new CurrentNewsCallback() {
                    @Override
                    public void onNewsFetched(List<CurrentNews> currentNews) {

                        // Show max 5 notifications at the same time
                        for (int i = 0; i < 5; i++) {
                            if (!currentNews.get(i).getId().equals(latestDocID)) {
                                NotificationHelper.showNewsNotification(currentNews.get(i), getContext());
                                RiksdagskollenApp.getInstance()
                                        .getAlertManager()
                                        .setAlertEnabledForSection(section, currentNews.get(0).getId(), true);
                            } else {
                                break;
                            }
                        }

                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFail(VolleyError error) {
                        countDownLatch.countDown();
                    }
                }, 1);
                break;

            default:
                countDownLatch.countDown();
        }
    }

    // Search for document trough documentid
    private void getDocument(String docid, final CountDownLatch countDownLatch) {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getDocument(docid, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                checkForReplies(documents.get(0));
            }

            @Override
            public void onFail(VolleyError error) {
                countDownLatch.countDown();
            }
        });
    }

    private void checkForReplies(final PartyDocument document) {
        RiksdagskollenApp.getInstance().getRiksdagenAPIManager().searchForReply(document, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                NotificationHelper.showReplyNotification(documents.get(0), getContext());
                RiksdagskollenApp.getInstance().getAlertManager().setAlertEnabledForDoc(document, false);
                countDownLatch.countDown();
            }

            @Override
            public void onFail(VolleyError error) {
                countDownLatch.countDown();
            }
        });
    }

    @Override
    protected void onCancel() {
        super.onCancel();
    }



    //Debug for check alerts job
    /*
    private void writeToFile(String data) {

        try {
            File debugFile = new File(Environment.getExternalStorageDirectory() + "/notification_debug.txt");
            if (!debugFile.exists()) {
                try {
                    debugFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fOut = new FileOutputStream(debugFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e)
        {
            System.out.println("Failed to write " + e.getMessage());
        }

    }*/

}
