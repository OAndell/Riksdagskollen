package oscar.riksdagskollen.Util.Twitter;

import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public interface TwitterTimeline {
    void getTimeline(TwitterCallback twitterCallback);
}
