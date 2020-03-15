package oscar.riksdagskollen.Util.WebTV;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.PlayerControlView;

public class DebateAudioPlayerView extends PlayerControlView implements DebatePlayer {


    public DebateAudioPlayerView(Context context) {
        super(context);
    }


    public DebateAudioPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setShowTimeoutMs(0);
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

}
