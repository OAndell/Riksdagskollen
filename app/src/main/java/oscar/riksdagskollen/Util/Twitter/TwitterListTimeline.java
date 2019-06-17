package oscar.riksdagskollen.Util.Twitter;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterListTimeline implements TwitterTimeline {

    private String ownerScreenName;
    private String slug;
    private long finalTweetID;

    public TwitterListTimeline(String ownerScreenName, String slug) {
        this.ownerScreenName = ownerScreenName;
        this.slug = slug;
        finalTweetID = -1;
    }

    @Override
    public void getTimeline(final TwitterCallback twitterCallback) {

        TwitterCallback localCallback = new TwitterCallback() {
            @Override
            public void onTweetsFetched(List<Tweet> tweets) {
                finalTweetID = tweets.get(tweets.size() - 1).getId();
                twitterCallback.onTweetsFetched(tweets);
                System.out.println(finalTweetID);
            }

            @Override
            public void onFail(VolleyError error) {
            }
        };

        if (finalTweetID == -1) {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetList(
                    ownerScreenName, slug, localCallback, false);
        } else {
            RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetListSinceID(
                    ownerScreenName, slug, localCallback, false, finalTweetID);

        }
    }

}
