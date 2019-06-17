package oscar.riksdagskollen.Util.Twitter;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterListTimeline implements TwitterTimeline {

    private String ownerScreenName;
    private String slug;

    public TwitterListTimeline(String ownerScreenName, String slug) {
        this.ownerScreenName = ownerScreenName;
        this.slug = slug;
    }

    @Override
    public void getTimeline(TwitterCallback twitterCallback) {
        RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetList(
                ownerScreenName, slug, twitterCallback, false);
    }

    @Override
    public void getTimelineNoRT(TwitterCallback twitterCallback) {
        RiksdagskollenApp.getInstance().getTwitterAPIManager().getTweetList(
                ownerScreenName, slug, twitterCallback, true);
    }
}
