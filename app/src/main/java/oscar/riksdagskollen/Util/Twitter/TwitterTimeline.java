package oscar.riksdagskollen.Util.Twitter;

import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

/**
 * This class tree acts as an interface to the TwitterAPI and should handle the processes of
 * getting the next batch of tweets in a twitter timeline and keeping track of the last
 * tweet downloaded.
 */
public abstract class TwitterTimeline {

    protected static long DEFAULT_TWEET_ID = -1;
    protected long finalTweetID;

    public TwitterTimeline() {
        this.finalTweetID = DEFAULT_TWEET_ID;
    }

    public void reset() {
        finalTweetID = DEFAULT_TWEET_ID;
    }

    /**
     * Get the next batch of tweets.
     *
     * @param twitterCallback Returns a list of tweets.
     */
    public abstract void getTimeline(TwitterCallback twitterCallback);
}
