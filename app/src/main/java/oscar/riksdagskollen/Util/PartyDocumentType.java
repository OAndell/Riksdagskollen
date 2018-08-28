package oscar.riksdagskollen.Util;

import java.util.ArrayList;

/**
 * Created by gustavaaro on 2018-08-28.
 */

public enum PartyDocumentType {

    Fraga("fr", "Skriftliga frågor"),
    Interpellation("ip", "Interpellationer"),
    Motion("mot","Motioner");

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

    public static CharSequence[] getDisplayNames(){
        return new CharSequence[]{Fraga.displayName,Interpellation.displayName,Motion.displayName};
    }
    public static ArrayList<PartyDocumentType> getAllDokTypes(){
        ArrayList<PartyDocumentType> filter = new ArrayList<>();
        filter.add(Fraga);
        filter.add(Interpellation);
        filter.add(Motion);

        return filter;
    }

}
