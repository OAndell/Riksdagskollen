package oscar.riksdagskollen.Util.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels.CurrentNews;
import oscar.riksdagskollen.CurrentNews.CurrentNewsListFragment;
import oscar.riksdagskollen.Fragment.VoteListFragment;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;

public class NotificationHelper {

    public static final String REPLIES_CHANNEL = "replies_channel";
    public static final String MONITOR_CHANNEL = "monitor_channel";

    public static final String NEWS_ITEM_URL_KEY = "news_item_url";
    public static final String SECTION_NAME_KEY = "section";
    public static final String DOCUMENT_KEY = "document";


    public static void showNewsNotification(CurrentNews currentNews, Context context) {
        createMonitorChannel(context);

        Intent intent = getIntent(context);
        intent.putExtra(NEWS_ITEM_URL_KEY, currentNews.getNewsUrl());
        intent.putExtra(SECTION_NAME_KEY, CurrentNewsListFragment.SECTION_NAME_NEWS);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(currentNews.getPublicerad());
            System.out.println("Date ->" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int resultCode = currentNews.getTitel().hashCode();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, resultCode, intent, 0);
        buildAndShowNotification(currentNews.getTitel(),
                context, pendingIntent, MONITOR_CHANNEL,
                Html.fromHtml(parseString(currentNews.getSummary())).toString(),
                date.getTime());

    }

    public static void showPartyNotification(String partyId, Context context) {
        createMonitorChannel(context);

        Intent intent = getIntent(context);
        intent.putExtra(SECTION_NAME_KEY, partyId);

        int resultCode = partyId.hashCode();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, resultCode, intent, 0);

        String title = String.format("%s har publicerat nya dokument.", CurrentParties.getParty(partyId).getName());

        buildAndShowNotification(title, context, pendingIntent, MONITOR_CHANNEL, "", 0);
    }

    public static void showVoteNotification(Vote vote, Context context) {
        createMonitorChannel(context);

        Intent intent = getIntent(context);
        intent.putExtra(SECTION_NAME_KEY, VoteListFragment.SECTION_NAME_VOTE);

        int resultCode = VoteListFragment.SECTION_NAME_VOTE.hashCode();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, resultCode, intent, 0);

        String title = "Det finns nya voteringar från riksdagen";
        String content = "Den senaste voteringen: " + vote.getTitel();


        buildAndShowNotification(title, context, pendingIntent, MONITOR_CHANNEL, content, 0);
    }

    public static void showReplyNotification(PartyDocument document, Context context) {
        createRepliesChannel(context);
        Intent intent = getIntent(context);
        intent.putExtra(DOCUMENT_KEY, document);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, REPLIES_CHANNEL)
                .setSmallIcon(R.drawable.riksdagskollen_logo_small)
                .setContentTitle("Svar på fråga: " + document.getTitel())
                .setContentText("Klicka här för att läsa svaret")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Klicka för att läsa svaret"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.valueOf(document.getBeteckning()), mBuilder.build());
    }

    private static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        //Needed for the extras to be passed for some reason
        intent.setAction("dummyAction");
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    private static void buildAndShowNotification(String title, Context context, PendingIntent intent, String channel, String content, long time) {

        if (content.isEmpty()) content = "Klicka här för att läsa";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.riksdagskollen_logo_small)
                .setContentTitle(title)
                .setContentText("Klicka här för att läsa")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setContentIntent(intent)
                .setShowWhen(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (time > 0) {
            mBuilder.setShowWhen(true);
            mBuilder.setWhen(time);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(title.hashCode(), mBuilder.build());
    }

    private static void createRepliesChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikationer för svar på frågor";
            String description = "Visar notifikationer när en fråga som användaren bevakar har blivit besvarad.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(REPLIES_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }

    private static void createMonitorChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikationer för bevakningar";
            String description = "Visar notifikationer för valda bevakningar.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MONITOR_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }

    private static String parseString(String s) {
        s = Html.fromHtml(s).toString().trim();
        s = s.replaceAll("<p>", "").replaceAll("</p>", "");
        return s;
    }


}


