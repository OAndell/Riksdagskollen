package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import oscar.riksdagskollen.R;


/**
 * A view for liking or disliking a document. Consist of two buttons and a textview.
 * The abstract class LikeListener is used for handling button presses.
 */
public class RateDocumentView extends LinearLayout {

    private ApprovalListener approvalListener;
    private Button likeButton;
    private Button disLikeButton;
    private TextView topTextTV;

    public RateDocumentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        inflate(context, R.layout.give_like_layout, this);
        setup();
    }

    public RateDocumentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.give_like_layout, this);
        setup();
    }

    public void setLikeListener(ApprovalListener approvalListener) {
        this.approvalListener = approvalListener;
    }

    private void setup() {
        topTextTV = findViewById(R.id.give_like_view_top_text);
        likeButton = findViewById(R.id.like_dialog_like_button);
        disLikeButton = findViewById(R.id.like_dialog_dislike_button);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalListener.onApprove();
                /*likeButton.setEnabled(false);
                disLikeButton.setEnabled(false);
                likeButton.setAlpha(.2f);
                disLikeButton.setAlpha(.5f);*/
            }
        });
        disLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalListener.onDisapprove();
                /*likeButton.setEnabled(false);
                disLikeButton.setEnabled(false);
                likeButton.setAlpha(.5f);
                disLikeButton.setAlpha(.2f);*/
            }
        });

        //Create default likeListener
        this.approvalListener = new ApprovalListener() {
            @Override
            public void onApprove() {
                System.out.println("No LikeListener defined, button press did nothing");
            }

            @Override
            public void onDisapprove() {
                System.out.println("No LikeListener defined, button press did nothing");
            }
        };
    }

    /**
     * Disable buttons and fade them.
     */
    public void disable() {
        likeButton.setEnabled(false);
        likeButton.setAlpha(.2f);
        disLikeButton.setEnabled(false);
        disLikeButton.setAlpha(.2f);
    }

    public void setTopText(String text) {
        topTextTV.setText(text);
    }


    public abstract static class ApprovalListener {
        public abstract void onApprove();

        public abstract void onDisapprove();
    }

}


