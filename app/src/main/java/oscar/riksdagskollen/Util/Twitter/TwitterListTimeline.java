package oscar.riksdagskollen.Util.Twitter;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterListTimeline extends TwitterTimeline {

    private String ownerScreenName;
    private String slug;
    /**
     * @param ownerScreenName twitter screen name
     * @param slug            name of the list
     */
    public TwitterListTimeline(String ownerScreenName, String slug) {
        this.ownerScreenName = ownerScreenName;
        this.slug = slug;
    }

    @Override
    public void getTimeline(final TwitterCallback twitterCallback) {

        TwitterCallback localCallback = new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                //Save last tweet ID
                finalTweetID = tweets.get(tweets.size() - 1).getId();
                twitterCallback.onTweetsFetched(tweets); //Return tweets
            }

            @Override
            public void onFail(VolleyError error) { }
        };

        if (finalTweetID == TwitterTimeline.DEFAULT_TWEET_ID) {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetList(
                    ownerScreenName, slug, localCallback, includeRT);
        } else {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetListSinceID(
                    ownerScreenName, slug, localCallback, includeRT, finalTweetID);

        }
    }


}
