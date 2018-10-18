package oscar.riksdagskollen.Util.Job;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.Manager.AlertManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;

/**
 * Created by gustavaaro on 2018-09-24.
 */

public class CheckAlertsJob extends Job {

    public static final String TAG = "job_alert";
    public static final String REPLIES_CHANNEL = "replies_channel";

    private CountDownLatch countDownLatch;
    private static final Map<String, String> partyIds;


    static {
        partyIds = new HashMap<>();
        partyIds.put("m", "Moderaterna");
        partyIds.put("s", "Socialdemokraterna");
        partyIds.put("sd", "Sverigedemokraterna");
        partyIds.put("kd", "Kristdemokraterna");
        partyIds.put("c", "Centerpartiet");
        partyIds.put("l", "Liberalerna");
        partyIds.put("v", "Vänsterpartiet");
        partyIds.put("mp", "Miljöpartiet");

    }

    public static void scheduleJob() {
        // Check once every 6 hour, be flexible within 30 minutes
        new JobRequest.Builder(CheckAlertsJob.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(6), TimeUnit.MINUTES.toMillis(30))
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
        countDownLatch = new CountDownLatch(alerts);

        Log.d(TAG, "onRunJob: alert count: " + alerts);
        if (alerts == 0) {
            cancel();
            return Result.SUCCESS;
        }


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
            //Do not wait longer than a minute
            countDownLatch.await(60, TimeUnit.SECONDS);
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
                    showMonitorNotification(partyId, true);
                    RiksdagskollenApp.getInstance()
                            .getAlertManager()
                            .setAlertEnabledForPartyDocuments(partyId, documents.get(0), true);
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
            case VoteListFragment.sectionName:
                RiksdagskollenApp.getInstance().getRiksdagenAPIManager().getVotes(new VoteCallback() {
                    @Override
                    public void onVotesFetched(List<Vote> votes) {
                        if (!votes.get(0).getId().equals(latestDocID)) {
                            showMonitorNotification(section, false);
                            RiksdagskollenApp.getInstance()
                                    .getAlertManager()
                                    .setAlertEnabledForSection(section, votes.get(0).getId(), true);
                            countDownLatch.countDown();
                        } else {
                            countDownLatch.countDown();
                        }
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
                showReplyNotification(documents.get(0));
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
        countDownLatch.countDown();
    }

    private void showReplyNotification(PartyDocument document) {

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("document", document);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikationer för svar på frågor";
            String description = "Visar notifikationer när en fråga som användaren bevakar har blivit besvarad.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(REPLIES_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), REPLIES_CHANNEL)
                .setSmallIcon(R.drawable.riksdagen_logo_yellow)
                .setContentTitle("Svar på fråga: " + document.getTitel())
                .setContentText("Klicka här för att läsa svaret")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Klicka för att läsa svaret"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(Integer.valueOf(document.getBeteckning()), mBuilder.build());
    }

    private void showMonitorNotification(String monitorSection, boolean isParty) {

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("section", monitorSection);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), monitorSection.hashCode(), intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikationer för bevakningar";
            String description = "Visar notifikationer för valda bevakningar.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(REPLIES_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        String message;
        if (isParty) {
            message = String.format("Nytt dokument från %s!", partyIds.get(monitorSection));
        } else {
            switch (monitorSection) {
                case VoteListFragment.sectionName:
                    message = "Ny votering!";
                    break;
                default:
                    message = "Nytt dokument!";
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), REPLIES_CHANNEL)
                .setSmallIcon(R.drawable.riksdagen_logo_yellow)
                .setContentTitle(message)
                .setContentText("Klicka här för att läsa")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Klicka här för att läsa"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(message.hashCode(), mBuilder.build());
    }

}
