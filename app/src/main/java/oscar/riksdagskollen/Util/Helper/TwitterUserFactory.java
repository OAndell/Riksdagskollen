package oscar.riksdagskollen.Util.Helper;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.JSONModel.Twitter.TwitterBridge;

public class TwitterUserFactory {
    public static TwitterBridge getUser(Party party) {
        switch (party.getID().toLowerCase()) {
            case "m":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.m_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "s":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.s_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "sd":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.sd_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "v":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.v_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "kd":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.kd_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "l":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.l_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "mp":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.mp_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            case "c":
                return new TwitterBridge(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.c_twitter),
                        TwitterBridge.TYPE_PARTY_TWITTER);
            default:
                return null;
        }
    }

    public static TwitterBridge getUser(Representative representative) {
        return null; //TODO not completed
    }
}
