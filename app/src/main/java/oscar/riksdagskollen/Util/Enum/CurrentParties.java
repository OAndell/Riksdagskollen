package oscar.riksdagskollen.Util.Enum;

import java.util.ArrayList;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Party;

public class CurrentParties {

    private static final Party M = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_m),
            "m", R.drawable.mlogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.m_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.m_ideology),
            "https://sv.wikipedia.org/wiki/Moderaterna");

    private static final Party S = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_s),
            "s", R.drawable.slogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.s_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.s_ideology),
            "https://sv.wikipedia.org/wiki/Socialdemokraterna_(Sverige)");

    private static final Party SD = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_sd),
            "sd", R.drawable.sdlogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.sd_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.sd_ideology),
            "https://sv.wikipedia.org/wiki/Sverigedemokraterna");

    private static final Party C = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_c),
            "c", R.drawable.clogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.c_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.c_ideology),
            "https://sv.wikipedia.org/wiki/Centerpartiet");

    private static final Party V = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_v),
            "v", R.drawable.vlogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.v_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.v_ideology),
            "https://sv.wikipedia.org/wiki/V%C3%A4nsterpartiet");

    private static final Party KD = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_kd),
            "kd", R.drawable.kdlogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.kd_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.kd_ideology),
            "https://sv.wikipedia.org/wiki/Kristdemokraterna_(Sverige)");

    private static final Party MP = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_mp),
            "mp", R.drawable.mplogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.mp_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.mp_ideology),
            "https://sv.wikipedia.org/wiki/Milj%C3%B6partiet");

    private static final Party L = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.party_l),
            "l", R.drawable.llogo,
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.l_website),
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.l_ideology),
            "https://sv.wikipedia.org/wiki/Liberalerna");

    private static final Party noParty = new Party(
            RiksdagskollenApp.getInstance().getApplicationContext().getString(R.string.no_party),
            "-", 0,
            "",
            "",
            "");

    private static final ArrayList<Party> parties = new ArrayList<Party>() {
        {
            add(M);
            add(L);
            add(C);
            add(KD);
            add(SD);
            add(MP);
            add(V);
            add(S);
            add(noParty);
        }
    };

    public static final String[] getPartyIDs() {
        String[] partyIDs = new String[parties.size()];
        for (int i = 0; i < parties.size(); i++) {
            partyIDs[i] = parties.get(i).getID();
        }
        return partyIDs;
    }

    ;



    public static ArrayList<Party> getParties() {
        return parties;
    }

    public static Party getM() {
        return M;
    }

    public static Party getS() {
        return S;
    }

    public static Party getSD() {
        return SD;
    }

    public static Party getC() {
        return C;
    }

    public static Party getV() {
        return V;
    }

    public static Party getKD() {
        return KD;
    }

    public static Party getMP() {
        return MP;
    }

    public static Party getL() {
        return L;
    }

    public static Party getNoParty() {
        return noParty;
    }

    public static Party getParty(String id) {
        switch (id.toLowerCase()) {
            case "m":
                return M;
            case "s":
                return S;
            case "sd":
                return SD;
            case "l":
                return L;
            case "mp":
                return MP;
            case "kd":
                return KD;
            case "v":
                return V;
            case "c":
                return C;
            case "-":
                return noParty;
            default:
                return OtherParties.getParty(id.toLowerCase());
        }
    }
}
