package oscar.riksdagskollen.Util.Twitter;

import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public abstract class TwitterTimeline {

    protected static long DEFAULT_TWEET_ID = -1;
    protected long finalTweetID;

    public TwitterTimeline() {
        this.finalTweetID = DEFAULT_TWEET_ID;
    }

    public void reset() {
        finalTweetID = DEFAULT_TWEET_ID;
    }

    public abstract void getTimeline(TwitterCallback twitterCallback);
}
