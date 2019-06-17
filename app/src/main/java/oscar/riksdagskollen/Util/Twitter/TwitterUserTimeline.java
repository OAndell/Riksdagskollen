package oscar.riksdagskollen.Util.Twitter;

import android.os.Parcelable;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterUserTimeline implements TwitterTimeline {
    public static int TYPE_PARTY_TWITTER = 10001;
    public static int TYPE_REP_TWITTER = 10002;

    private Parcelable owner;
    private String twitterScreenName;
    private int type;
    private long finalTweetID;

    public TwitterUserTimeline(Parcelable owner, String twitterScreenName, int type) {
        this.owner = owner;
        this.twitterScreenName = twitterScreenName;
        this.type = type;
        finalTweetID = -1;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public Parcelable getOwner() {
        return owner;
    }

    public int getType() {
        return type;
    }

    public void getTimeline(final TwitterCallback callback) {

        TwitterCallback localCallback = new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                finalTweetID = tweets.get(tweets.size() - 1).getId();
                callback.onTweetsFetched(tweets);
            }

            @Override
            public void onFail(VolleyError error) {
            }
        };

        if (finalTweetID == -1) {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweets(
                    twitterScreenName, localCallback, false);
        } else {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetsSinceID(
                    twitterScreenName, localCallback, false, finalTweetID);
        }

    }


}
