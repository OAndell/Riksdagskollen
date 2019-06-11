package oscar.riksdagskollen.Util.Helper;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;
import oscar.riksdagskollen.Util.JSONModel.Twitter.TwitterUser;

public class TwitterUserFactory {
    public static TwitterUser getUser(Party party) {
        switch (party.getID().toLowerCase()) {
            case "m":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.m_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "s":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.s_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "sd":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.sd_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "v":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.v_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "kd":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.kd_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "l":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.l_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "mp":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.mp_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            case "c":
                return new TwitterUser(party,
                        RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.c_twitter),
                        TwitterUser.TYPE_PARTY_TWITTER);
            default:
                return null;
        }
    }

    public static TwitterUser getUser(Representative representative) {
        return null; //TODO not completed
    }
}
