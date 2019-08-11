package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import oscar.riksdagskollen.Manager.AnalyticsManager;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.AnimUtil;
import oscar.riksdagskollen.Util.RiksdagenCallback.RateResultListener;

public class PlayRatingQuestionView extends CardView {
    private TextView question;
    private AppCompatButton positive;
    private AppCompatButton negative;
    private AppCompatButton neutral;
    private RateResultListener rateResultListener;
    private AnalyticsManager analyticsManager;
    private PlayRatingQuestionView questionView = this;
    private Context context;

    private final static String RATING_REQUEST_SHOWED = "RATING_REQUEST_SHOWED";

    private final static String RATING_REQUEST_ANSWER_LIKE = "RATING_REQUEST_ANSWER_LIKE";
    private final static String RATING_REQUEST_ANSWER_DISLIKE = "RATING_REQUEST_ANSWER_DISLIKE";
    private final static String RATING_REQUEST_ASK_LATER = "RATING_REQUEST_ASK_LATER";

    private final static String LEAVE_RATING_ANSWER_YES = "LEAVE_RATING_ANSWER_YES";
    private final static String LEAVE_RATING_ANSWER_NO = "LEAVE_RATING_ANSWER_NO";
    private final static String LEAVE_RATING_ANSWER_MAYBE_LATER = "LEAVE_RATING_ANSWER_MAYBE_LATER";

    private final static String FEEDBACK_REQUEST_ANSWER_YES = "FEEDBACK_REQUEST_ANSWER_YES";
    private final static String FEEDBACK_REQUEST_ANSWER_NO = "FEEDBACK_REQUEST_ANSWER_NO";
    private final static String FEEDBACK_REQUEST_ANSWER_MAYBE_LATER = "FEEDBACK_REQUEST_ANSWER_YES";


    public PlayRatingQuestionView(@NonNull Context context) {
        super(context);
        this.context = context;

        View rateView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.review_question_view, null);
        this.addView(rateView);

        analyticsManager = AnalyticsManager.getInstance();
        analyticsManager.logEvent(RATING_REQUEST_SHOWED, null);

        question = rateView.findViewById(R.id.question);
        positive = rateView.findViewById(R.id.positive_button);
        negative = rateView.findViewById(R.id.negative_button);
        neutral = rateView.findViewById(R.id.neutral_button);

        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                askToReview();
                analyticsManager.logEvent(RATING_REQUEST_ANSWER_LIKE, null);
            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                askToGiveFeedback();
                analyticsManager.logEvent(RATING_REQUEST_ANSWER_DISLIKE, null);
            }
        });

        neutral.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rateResultListener.onResult();
                RiksdagskollenApp.getInstance().remindLater();
                analyticsManager.logEvent(RATING_REQUEST_ASK_LATER, null);
            }
        });

    }

    public void setRateResultListener(RateResultListener rateResultListener) {
        this.rateResultListener = rateResultListener;
    }


    private void askToReview() {

        AnimUtil.fadeOut(this, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                question.setText(R.string.feedback_positive_question);
                positive.setText(R.string.absolutely);
                negative.setText(R.string.no_thanks);
                neutral.setText(R.string.maybe_later);

                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leavePlayReview();
                        analyticsManager.logEvent(LEAVE_RATING_ANSWER_YES, null);

                    }
                });

                negative.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rateResultListener.onResult();
                        analyticsManager.logEvent(LEAVE_RATING_ANSWER_NO, null);
                        disableRatingQuestion();
                    }
                });

                neutral.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rateResultListener.onResult();
                        RiksdagskollenApp.getInstance().remindLater();
                        analyticsManager.logEvent(LEAVE_RATING_ANSWER_MAYBE_LATER, null);
                    }
                });

                AnimUtil.fadeIn(questionView, null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    private void leavePlayReview() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=se.oandell.riksdagen"));
        intent.setPackage("com.android.vending");
        try {
            context.startActivity(intent);
            rateResultListener.onResult();
            disableRatingQuestion();
        } catch (Exception e) {
            rateResultListener.onResult();
            RiksdagskollenApp.getInstance().remindLater();
            Toast.makeText(context, "Kunde inte öppna Google Play", Toast.LENGTH_LONG).show();
        }

    }

    private void askToGiveFeedback() {

        AnimUtil.fadeOut(this, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                question.setText(R.string.feedback_negative_question);
                positive.setText(R.string.absolutely);
                negative.setText(R.string.no_thanks);

                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendFeedback();
                        analyticsManager.logEvent(FEEDBACK_REQUEST_ANSWER_YES, null);
                    }
                });

                negative.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rateResultListener.onResult();
                        disableRatingQuestion();
                        analyticsManager.logEvent(FEEDBACK_REQUEST_ANSWER_NO, null);
                    }
                });

                neutral.setText(R.string.maybe_later);
                neutral.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rateResultListener.onResult();
                        RiksdagskollenApp.getInstance().remindLater();
                        analyticsManager.logEvent(FEEDBACK_REQUEST_ANSWER_MAYBE_LATER, null);
                    }
                });

                AnimUtil.fadeIn(questionView, null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void sendFeedback() {

        String versionName = "";
        try {
            PackageInfo pInfo = null;
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "oscar@andell.eu", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tankar kring Riksdagskollen");
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                "\n\nSysteminformation:" +
                        "\nApp-version: " + versionName +
                        "\nSdk-version: " + Build.VERSION.SDK_INT);
        context.startActivity(Intent.createChooser(emailIntent, "Skicka ett mail"));
        rateResultListener.onResult();
        disableRatingQuestion();
    }

    private void disableRatingQuestion() {
        RiksdagskollenApp.getInstance().disableRatingQuestion();
    }


}
