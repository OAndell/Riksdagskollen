package oscar.riksdagskollen.Util.Twitter;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Party;

public class TwitterTimelineFactory {

    public static final int LIST_RIKSDAGEN_ALL = 101;
    public static final int LIST_RIKSDAGEN_PARTIES = 102;



    public static TwitterUserTimeline getUser(Party party) {
        switch (party.getID().toLowerCase()) {
            case "m":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.m_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "s":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.s_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "sd":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.sd_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "v":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.v_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "kd":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.kd_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "l":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.l_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "mp":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.mp_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            case "c":
                return new TwitterUserTimeline(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.c_twitter),
                        TwitterUserTimeline.TYPE_PARTY_TWITTER);
            default:
                return null;
        }
    }

    public static TwitterListTimeline getTwitterList(int type) {
        switch (type) {
            case LIST_RIKSDAGEN_PARTIES:
                return new TwitterListTimeline("riksdagskollen", "riksdagskollen");
            case LIST_RIKSDAGEN_ALL:
                return new TwitterListTimeline("Riksdagskollen", "riksdagskollen-ledamoter");
            default:
                return null;
        }
    }

    public static TwitterUserTimeline getUser(Representative representative) {
        return null; //TODO not completed
    }
}
