package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import oscar.riksdagskollen.R;

public class LikeDialogView extends LinearLayout {

    private LikeListener likeListener;
    private Button showDialogButton;

    public LikeDialogView(Context context, Button showDialogButton) {
        super(context);
        this.showDialogButton = showDialogButton;
        setup(context);
    }

    public LikeDialogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        inflate(context, R.layout.like_dialog_button, this);
        setup(context);
    }

    public LikeDialogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(context, R.layout.like_dialog_button, this);
        showDialogButton = findViewById(R.id.like_dialog_button);
        setup(context);
    }

    public void setLikeListener(LikeListener likeListener) {
        this.likeListener = likeListener;
    }

    private void setup(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.rate_question)
                .setTitle(R.string.rate_question)
                .setPositiveButton(R.string.good_prop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        likeListener.onLike();
                    }
                })
                .setNegativeButton(R.string.bad_prop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        likeListener.onDislike();
                    }
                });
        final AlertDialog dialog = builder.create();
        showDialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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


