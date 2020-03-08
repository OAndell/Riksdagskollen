package oscar.riksdagskollen.Util.WebTV;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.R;

public class JSInterface {

    public static final int NOTIF_ID = 99;

    public static boolean playing;
    private Context context;
    private String debateTitle;
    private String subtitle;
    private NotificationManagerCompat notificationManager;

    public JSInterface(Context context, String debateTitle, String subtitle) {
        this.context = context;
        this.debateTitle = debateTitle;
        this.subtitle = subtitle;
        notificationManager = NotificationManagerCompat.from(context);

    }


    private String CHANNEL_ID = "WEB_DEBATE_MEDIA_PLAYER";

    @JavascriptInterface
    public void mediaAction(String type) {
        Log.e("Info", type);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                return;
            }
        }
        playing = Boolean.parseBoolean(type);

        if (playing) {
            notificationManager.notify(NOTIF_ID, getPlayingNotification());
        } else {
            notificationManager.notify(NOTIF_ID, getPausedNotification());
        }
    }

    public void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mediaspelare för debatt i Riksdagens Web-TV";
            String description = "Visar notifikation med kontroller för mediaspelaren.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification getPlayingNotification() {
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_PAUSE), 0);

        PendingIntent seekForwardPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_SEEK_FORWARD), 0);
        PendingIntent seekBackwardPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_SEEK_BACKWARD), 0);

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.riksdagskollen_logo_small)
                .setContentTitle(debateTitle)
                .setContentText("Spelar debatt från riksdagens Webb-TV")
                .setContentInfo(subtitle)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_replay, "Hoppa bakåt", seekBackwardPendingIntent)
                .addAction(R.drawable.ic_pause, "Pausa", pausePendingIntent)
                .addAction(R.drawable.ic_forward, "Hoppa framåt", seekForwardPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1));
        return builder.build();
    }

    public Notification getPausedNotification() {

        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_PLAY), 0);

        PendingIntent seekForwardPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_SEEK_FORWARD), 0);
        PendingIntent seekBackwardPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(DebateWebTvView.ACTION_SEEK_BACKWARD), 0);

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.riksdagskollen_logo_small)
                .setContentTitle(debateTitle)
                .setContentText("Spelar debatt från riksdagens Webb-TV")
                .setContentInfo(subtitle)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_replay, "Hoppa bakåt", seekBackwardPendingIntent)
                .addAction(R.drawable.ic_play, "Spela", playPendingIntent)
                .addAction(R.drawable.ic_forward, "Hoppa framåt", seekForwardPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1));
        return builder.build();
    }
}
