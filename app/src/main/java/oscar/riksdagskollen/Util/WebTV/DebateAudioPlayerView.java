package oscar.riksdagskollen.Util.WebTV;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import oscar.riksdagskollen.Activity.MainActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

public class DebateAudioPlayerView extends PlayerControlView implements DebatePlayer {

    private MediaSource audioMediaSource;

    public DebateAudioPlayerView(Context context) {
        super(context);
    }

    private String CHANNEL_ID = "WEB_DEBATE_AUDIO_PLAYER";

    private String URL = "";
    private PartyDocument debateDocument;


    public DebateAudioPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDebate(PartyDocument debate) {
        this.debateDocument = debate;
    }

    public void setupMediaSource(String audioSourceUrl) {
        String userAgent = Util.getUserAgent(getContext(), getContext().getString(R.string.app_name));
        audioMediaSource =
                new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(getContext(), userAgent))
                        .createMediaSource(Uri.parse(audioSourceUrl));
        if (getPlayer() != null) ((SimpleExoPlayer) getPlayer()).prepare(audioMediaSource);
    }


    public void setupPlayer() {
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(getContext()).build();
        setPlayer(player);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .build();
        player.setAudioAttributes(audioAttributes);

        setShowTimeoutMs(0);

        setupNotificationChannel();

        PlayerNotificationManager notificationManager = new PlayerNotificationManager(
                getContext(),
                CHANNEL_ID,
                99,
                new DescriptionAdapter());

        notificationManager.setSmallIcon(R.drawable.riksdagskollen_logo_small);
        notificationManager.setPlayer(player);
        if (audioMediaSource != null) player.prepare(audioMediaSource);
    }


    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mediaspelare för debatt i Riksdagen.";
            String description = "Visar notifikation med kontroller för mediaspelaren.";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void pause() {
        if (getPlayer() != null) {
            getPlayer().setPlayWhenReady(false);
        }
    }

    @Override
    public void play() {
        if (getPlayer() != null) {
            getPlayer().setPlayWhenReady(true);
        }
    }

    @Override
    public void fastForward() {
        if (getPlayer() != null) {
            getPlayer().next();
        }
    }

    @Override
    public void fastRewind() {
        if (getPlayer() != null) {
            getPlayer().previous();
        }
    }

    @Override
    public void seekTo(int seconds) {
        if (getPlayer() != null) {
            getPlayer().seekTo(seconds * 1000);
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
            final Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
            return contentIntent;
        }
    }


}
