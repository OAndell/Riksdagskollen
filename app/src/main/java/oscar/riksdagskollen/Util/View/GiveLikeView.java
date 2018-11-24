package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import oscar.riksdagskollen.R;

public class GiveLikeView extends LinearLayout {

    private LikeListener likeListener;
    private Button likeButton;
    private Button disLikeButton;


    public GiveLikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        inflate(context, R.layout.give_like_layout, this);
        setup(context);
    }

    public GiveLikeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.give_like_layout, this);
        setup(context);
    }

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    private void setup(Context context) {

        likeButton = findViewById(R.id.like_dialog_like_button);
        disLikeButton = findViewById(R.id.like_dialog_dislike_button);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeListener.onLike();
            }
        });
        disLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeListener.onDislike();
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


    public abstract class LikeListener {
        public abstract void onLike();

        public abstract void onDislike();
    }

}


