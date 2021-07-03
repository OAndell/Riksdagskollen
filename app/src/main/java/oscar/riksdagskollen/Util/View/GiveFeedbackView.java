package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Util.Helper.HelperFunctionsKt;

public class GiveFeedbackView extends LinearLayout {
    public GiveFeedbackView(Context context) {
        super(context);
        inflate(context, R.layout.give_feedback_view, this);
        configureView(context);
    }

    public void configureView(final Context context) {
        final ImageView twitterImage = findViewById(R.id.imageButtonTwitter);
        final ImageView emailImage = findViewById(R.id.imageButtonEmail);

        twitterImage.setOnClickListener(view -> {
            String tweetUrl = "https://twitter.com/intent/tweet?text=@Riksdagskollen";
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
            context.startActivity(twitterIntent);
        });

        emailImage.setOnClickListener(view -> {
            Intent emailIntent = HelperFunctionsKt.createDeveloperEmailIntent(context.getResources().getString(R.string.dev_email_1));
            context.startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"));
        });
    }
}
