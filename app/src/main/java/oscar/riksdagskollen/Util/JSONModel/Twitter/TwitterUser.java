package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcelable;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterUser {
    public static int TYPE_PARTY_TWITTER = 10001;
    public static int TYPE_REP_TWITTER = 10002;

    private Parcelable owner;
    private String twitterScreenName;
    private int type;
    //private ArrayList<Tweet> tweets;

    public TwitterUser(Parcelable owner, String twitterScreenName, int type) {
        this.owner = owner;
        this.twitterScreenName = twitterScreenName;
        this.type = type;
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

    public void getTimeline(TwitterCallback callback) {
        RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweets(twitterScreenName, callback);
    }
}
