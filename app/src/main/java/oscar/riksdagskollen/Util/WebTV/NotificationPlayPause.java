package oscar.riksdagskollen.Util.WebTV;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RemoteViews;

import oscar.riksdagskollen.R;

import static oscar.riksdagskollen.Util.WebTV.JSInterface.playing;

public class NotificationPlayPause extends BroadcastReceiver {

    private WebView wv;


    @Override
    public void onReceive(Context activity, Intent intent) {
        Log.e("Here", "I am here");

        if (playing) {


            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity);
            Notification notification = builder.getNotification();
            notification.icon = R.drawable.riksdagskollen_logo_small;

            RemoteViews contentView = new RemoteViews(activity.getPackageName(), R.layout.web_tv_notification);
            notification.contentView = contentView;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            contentView.setTextViewText(R.id.title, "Browser");
            contentView.setTextViewText(R.id.desc, "This is a description. - PAUSED");
            contentView.setImageViewResource(R.id.pausePlay, R.drawable.ic_play);

            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(activity, 0, new Intent(activity, NotificationPlayPause.class), 0);
            contentView.setOnClickPendingIntent(R.id.pausePlay, pendingSwitchIntent);

            mNotificationManager.notify(99, notification);

            playing = false;
        } else {


            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity);
            Notification notification = builder.getNotification();
            notification.icon = R.drawable.riksdagskollen_logo_small;

            RemoteViews contentView = new RemoteViews(activity.getPackageName(), R.layout.web_tv_notification);
            notification.contentView = contentView;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            contentView.setTextViewText(R.id.title, "Browser");
            contentView.setTextViewText(R.id.desc, "This is a description. - PLAYING");
            contentView.setImageViewResource(R.id.pausePlay, R.drawable.ic_play);


            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(activity, 0, new Intent(activity, NotificationPlayPause.class), 0);
            contentView.setOnClickPendingIntent(R.id.pausePlay, pendingSwitchIntent);

            mNotificationManager.notify(99, notification);

            playing = true;
        }
    }
}
