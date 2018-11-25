package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import oscar.riksdagskollen.R;

public class GiveLikeView extends LinearLayout {

    private LikeListener likeListener;
    private Button likeButton;
    private Button disLikeButton;
    private TextView topTextTV;


    public GiveLikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        inflate(context, R.layout.give_like_layout, this);
        setup();
    }

    public GiveLikeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.give_like_layout, this);
        setup();
    }

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    private void setup() {
        topTextTV = findViewById(R.id.give_like_view_top_text);
        likeButton = findViewById(R.id.like_dialog_like_button);
        disLikeButton = findViewById(R.id.like_dialog_dislike_button);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeListener.onLike();
                likeButton.setEnabled(false);
                disLikeButton.setEnabled(false);
                likeButton.setAlpha(.2f);
                disLikeButton.setAlpha(.5f);
            }
        });
        disLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeListener.onDislike();
                likeButton.setEnabled(false);
                disLikeButton.setEnabled(false);
                likeButton.setAlpha(.5f);
                disLikeButton.setAlpha(.2f);
            }
        });

        //Create default likeListener
        this.likeListener = new LikeListener() {
            @Override
            public void onLike() {
                System.out.println("LIKE");
            }

            @Override
            public void onDislike() {
                System.out.println("DISLIKE");
            }
        };
    }

    public void disable() {
        likeButton.setEnabled(false);
        likeButton.setAlpha(.2f);
        disLikeButton.setEnabled(false);
        disLikeButton.setAlpha(.2f);
    }

    public void setTopText(String text) {
        topTextTV.setText(text);
    }


    public abstract static class LikeListener {
        public abstract void onLike();

        public abstract void onDislike();
    }

}


