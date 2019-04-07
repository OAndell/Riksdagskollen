package oscar.riksdagskollen.Util.Enum;

import java.util.ArrayList;

import oscar.riksdagskollen.Util.JSONModel.Party;

public class PartyHelper {

    /**
     * @return ArrayList<Party> of all parties ( including noParty and older parties)
     */
    public static ArrayList<Party> getCompletePartyList() {
        ArrayList<Party> partiesComplete = new ArrayList<>();
        partiesComplete.addAll(CurrentParties.getParties());
        partiesComplete.addAll(OtherParties.getOtherParties());
        return partiesComplete;
    }

    public static String[] getCompletePartyIDs() {
        ArrayList<Party> partiesComplete = getCompletePartyList();
        String[] ids = new String[partiesComplete.size()];
        for (int i = 0; i < partiesComplete.size(); i++) {
            ids[i] = partiesComplete.get(i).getID();
        }
        return ids;
    }


}
