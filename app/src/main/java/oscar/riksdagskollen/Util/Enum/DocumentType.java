package oscar.riksdagskollen.Util.Enum;

import java.util.ArrayList;
import java.util.Arrays;

import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by gustavaaro on 2018-08-28.
 */

public enum DocumentType {

    Fraga("fr", "Skriftliga frågor"),
    Interpellation("ip", "Interpellationer"),
    Motion("mot", "Motioner"),
    FragaSvar("frs", "Svar på fråga"),
    Betankande("bet", "Betänkande"),
    KamAd("kam-ad", "Aktuell Debatt");

    private String docType;
    private String displayName;

    DocumentType(String docType, String displayName) {
        this.docType = docType;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDocType() {
        return docType;
    }

    public static DocumentType getDocumentTypeFromName(CharSequence displayName) {
        for (DocumentType type : DocumentType.values()) {
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

    public static DocumentType getDocTypeForDocument(PartyDocument document) {

        for (int i = 0; i < values().length; i++) {
            if (document.getDoktyp().equals(values()[i].docType)) {
                return values()[i];
            }
        }
        return null;
    }

    public static ArrayList<DocumentType> getPartyDokTypes() {
        ArrayList<DocumentType> filter = new ArrayList<>();
        filter.add(Fraga);
        filter.add(Interpellation);
        filter.add(Motion);
        return filter;
    }

    public static ArrayList<DocumentType> getAllDokTypes() {
        ArrayList<DocumentType> filter = new ArrayList<>();
        filter.addAll(Arrays.asList(values()));
        return filter;
    }


}
