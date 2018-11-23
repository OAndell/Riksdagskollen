package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import oscar.riksdagskollen.R;

public class LikeBar extends LinearLayout {

    private ProgressBar bar;
    private TextView percentTV;

    private int likes = 0;
    private int dislikes = 0;
    private int percent = 0;

    public LikeBar(Context context) {
        super(context);
        setup(context);
    }

    public LikeBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setup(context);
    }

    public LikeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    private void setup(Context context) {
        inflate(context, R.layout.likebar, this);
        bar = findViewById(R.id.likebar_progressBar);
        percentTV = findViewById(R.id.likebar_percent);
        if (bar == null && percentTV == null) {
            bar = new ProgressBar(context);
            percentTV = new TextView(context);
        }
        setRatio();
    }

    public void setRatio(int likes, int dislikes) {
        this.likes = likes;
        this.dislikes = dislikes;
        setRatio();
    }

    private void setRatio() {
        if (likes == 0 & dislikes == 0) {
            this.setVisibility(GONE);
        } else {
            this.setVisibility(VISIBLE);
            percent = Math.round((float) likes / (likes + dislikes) * 100);
            bar.setProgress(percent);
            percentTV.setText(percent + "%");
        }


    }

    public void like() {
        likes++;
        setRatio();
    }

    public void dislike() {
        dislikes++;
        setRatio();
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public int getPercent() {
        return percent;
    }


}
