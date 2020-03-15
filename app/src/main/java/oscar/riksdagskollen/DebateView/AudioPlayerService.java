package oscar.riksdagskollen.DebateView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

public class AudioPlayerService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private SimpleExoPlayer player;
    private PartyDocument debateDocument;
    private String audioSourceUrl = null;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSource audioMediaSource;
    private Context context = this;

    private String CHANNEL_ID = "WEB_DEBATE_AUDIO_PLAYER";

    public static final String DEBATE_DOCUMENT = "DEBATE_DOCUMENT";
    public static final String AUDIO_SOURCE_URL = "AUDIO_SOURCE_URL";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    private void releasePlayer() {
        if (player != null) {
            playerNotificationManager.setPlayer(null);
            player.release();
            player = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SimpleExoPlayer getPlayerInstance() {
        if (player == null) {
            startPlayer();
        }
        return player;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        releasePlayer();
        debateDocument = intent.getParcelableExtra(DEBATE_DOCUMENT);
        audioSourceUrl = intent.getStringExtra(AUDIO_SOURCE_URL);
        if (player == null) {
            startPlayer();
        }
        return START_STICKY;
    }

    private void startPlayer() {
        final Context context = this;
        player = new SimpleExoPlayer.Builder(this).build();

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)));

        audioMediaSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(audioSourceUrl));

        player.prepare(audioMediaSource);
        player.setPlayWhenReady(false);
        setupNotificationChannel();

        playerNotificationManager = new PlayerNotificationManager(
                context,
                CHANNEL_ID,
                99,
                new DescriptionAdapter(), new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                stopSelf();
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                if (player.getPlayWhenReady()) {
                    startForeground(notificationId, notification);
                }
            }
        });

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (!playWhenReady) {
                    stopForeground(false);
                }
            }


        });


        playerNotificationManager.setSmallIcon(R.drawable.riksdagskollen_logo_small);
        playerNotificationManager.setPlayer(player);
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }


    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mediaspelare för debatt i Riksdagen.";
            String description = "Visar notifikation med kontroller för mediaspelaren.";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private class DescriptionAdapter implements
            PlayerNotificationManager.MediaDescriptionAdapter {


        @Override
        public String getCurrentContentTitle(Player player) {
            return debateDocument == null ? "Riksdagsdebatt" : debateDocument.getTitel();
        }

        @Nullable
        @Override
        public String getCurrentContentText(Player player) {
            return "Spelar debatt från riksdagen";
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player,
                                          PlayerNotificationManager.BitmapCallback callback) {
            return null;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            final Intent intent = new Intent(context, MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
            return contentIntent;
        }
    }
}
