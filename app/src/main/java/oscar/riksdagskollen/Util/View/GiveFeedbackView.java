package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import oscar.riksdagskollen.BuildConfig;
import oscar.riksdagskollen.R;

public class GiveFeedbackView extends LinearLayout {
    public GiveFeedbackView(Context context) {
        super(context);
        inflate(context, R.layout.give_feedback_view, this);
        configureView(context);
    }

    public void configureView(final Context context) {
        final ImageView twitterImage = findViewById(R.id.imageButtonTwitter);
        final ImageView emailImage = findViewById(R.id.imageButtonEmail);

        twitterImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetUrl = "https://twitter.com/intent/tweet?text=@Riksdagskollen";
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                context.startActivity(twitterIntent);
            }
        });

        emailImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "oscar@andell.eu", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tankar kring Riksdagskollen");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "\n\nSysteminformation:" +
                                "\nApp-version: " + BuildConfig.VERSION_NAME +
                                "\nSdk-version: " + Build.VERSION.SDK_INT);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"));
            }
        });
    }
}
