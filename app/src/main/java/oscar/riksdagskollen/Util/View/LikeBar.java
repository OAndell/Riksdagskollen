package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import oscar.riksdagskollen.R;

public class LikeBar extends LinearLayout {

    private ProgressBar bar;
    private int progress = 0;

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
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setProgress(progress += 10);
            }
        });
    }

    public void setRatio(int progress) {
        this.progress = progress;
        bar.setProgress(progress);
    }

    public void like() {
        bar.setProgress(progress++);
    }

    public void dislike() {
        bar.setProgress(progress--);
    }


}
