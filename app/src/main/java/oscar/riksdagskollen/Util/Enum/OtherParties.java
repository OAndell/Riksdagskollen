package oscar.riksdagskollen.Util.Enum;

import java.util.ArrayList;

import oscar.riksdagskollen.Util.JSONModel.Party;

public class OtherParties {

    private static final Party FP = new Party("Folkpartiet",
            "fp", 0,
            "", "",
            "");


    private static final ArrayList<Party> otherParties = new ArrayList<Party>() {
        {
            add(FP);
        }
    };


    public static Party getFp() {
        return FP;
    }

    public static ArrayList<Party> getOtherParties() {
        return otherParties;
    }

    public static Party getParty(String id) {
        switch (id.toLowerCase()) {
            case "fp":
                return FP;
            default:
                return null;
        }
    }

}
