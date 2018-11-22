package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import oscar.riksdagskollen.R;

public class LikeBar extends LinearLayout {

    private ProgressBar bar;
    private TextView percentTV;
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
        percentTV = findViewById(R.id.likebar_percent);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRatio(progress += 10);
            }
        });
    }

    public void setRatio(int progress) {
        this.progress = progress;
        percentTV.setText(progress + "%");
        bar.setProgress(progress);
    }

    public void like() {
        setRatio(progress++);
    }

    public void dislike() {
        setRatio(progress--);
    }


}
