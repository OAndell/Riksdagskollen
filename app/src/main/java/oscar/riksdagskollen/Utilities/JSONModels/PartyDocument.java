package oscar.riksdagskollen.Utilities.JSONModels;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class PartyDocument {

    String id;
    String undertitel;
    String titel;
    String typ;
    String publicerad;
    String doktyp;
    String dokument_url_text;
    String dokument_url_html;
    String traff;
    String summary;
    String dokumentnamn;

    public String getId() {
        return id;
    }

    public String getDoktyp() {
        return doktyp;
    }

    public String getUndertitel() {
        return undertitel;
    }

    public String getTitel() {
        return titel;
    }

    public String getTyp() {
        return typ;
    }

    public String getPublicerad() {
        return publicerad;
    }

    public String getDokument_url_text() {
        return dokument_url_text;
    }

    public String getDokument_url_html() {
        return dokument_url_html;
    }

    public String getTraff() {
        return traff;
    }

    public String getSummary() {
        return summary;
    }

    public String getDokumentnamn() {
        return dokumentnamn;
    }

    @Override
    public String toString() {
        return getTitel();
    }
}
