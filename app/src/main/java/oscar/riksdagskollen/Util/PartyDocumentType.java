package oscar.riksdagskollen.Util;

import java.util.ArrayList;
import java.util.Arrays;

import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by gustavaaro on 2018-08-28.
 */

public enum PartyDocumentType {

    Fraga("fr", "Skriftliga frågor"),
    Interpellation("ip", "Interpellationer"),
    Motion("mot", "Motioner"),
    FragaSvar("frs", "Svar på fråga");

    private String docType;
    private String displayName;

    PartyDocumentType(String docType, String displayName){
        this.docType = docType;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDocType() {
        return docType;
    }

    public static PartyDocumentType getDocumentTypeFromName(CharSequence displayName){
        for (PartyDocumentType type: PartyDocumentType.values()) {
            if (type.displayName.equals(displayName)) return type;
        }
        return null;
    }

    public static CharSequence[] getPartyDisplayNames() {
        CharSequence[] names = new CharSequence[getPartyDokTypes().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = getPartyDokTypes().get(i).displayName;
        }

        return names;
    }

    public static CharSequence[] getDisplayNames(){
        CharSequence[] names = new CharSequence[getAllDokTypes().size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = values()[i].displayName;
        }

        return names;
    }

    public static PartyDocumentType getDocTypeForDocument(PartyDocument document) {

        for (int i = 0; i < values().length; i++) {
            if (document.getDoktyp().equals(values()[i].docType)) {
                return values()[i];
            }
        }
        return null;
    }

    public static ArrayList<PartyDocumentType> getPartyDokTypes() {
        ArrayList<PartyDocumentType> filter = new ArrayList<>();
        filter.addAll(Arrays.asList(values()));
        // Party documents do not have Answers for questions
        filter.remove(FragaSvar);
        return filter;
    }

    public static ArrayList<PartyDocumentType> getAllDokTypes(){
        ArrayList<PartyDocumentType> filter = new ArrayList<>();
        filter.addAll(Arrays.asList(values()));
        return filter;
    }


}
