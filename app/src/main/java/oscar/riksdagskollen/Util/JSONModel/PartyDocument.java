package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class PartyDocument extends java.lang.Object implements Parcelable {

    private String id;
    private String undertitel;
    private final String titel;
    private String rm;
    private String typ;
    private String beteckning;
    private final String publicerad;
    private String doktyp;
    private String dokument_url_text;
    private String dokument_url_html;
    private String traff;
    private String summary;
    private String dokumentnamn;
    private String datum;
    private DokIntressent dokintressent;
    private long saved = 0;
    private Debate debatt;
    private String debattnamn;
    private String debattdag;
    private String database;

    protected PartyDocument(Parcel in) {
        id = in.readString();
        undertitel = in.readString();
        titel = in.readString();
        rm = in.readString();
        typ = in.readString();
        beteckning = in.readString();
        publicerad = in.readString();
        doktyp = in.readString();
        dokument_url_text = in.readString();
        dokument_url_html = in.readString();
        traff = in.readString();
        summary = in.readString();
        dokumentnamn = in.readString();
        datum = in.readString();
        dokintressent = in.readParcelable(DokIntressent.class.getClassLoader());
        saved = in.readLong();
        debatt = in.readParcelable(Debate.class.getClassLoader());
        debattnamn = in.readString();
        debattdag = in.readString();
        database = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(undertitel);
        dest.writeString(titel);
        dest.writeString(rm);
        dest.writeString(typ);
        dest.writeString(beteckning);
        dest.writeString(publicerad);
        dest.writeString(doktyp);
        dest.writeString(dokument_url_text);
        dest.writeString(dokument_url_html);
        dest.writeString(traff);
        dest.writeString(summary);
        dest.writeString(dokumentnamn);
        dest.writeString(datum);
        dest.writeParcelable(dokintressent, flags);
        dest.writeLong(saved);
        dest.writeParcelable(debatt, flags);
        dest.writeString(debattnamn);
        dest.writeString(debattdag);
        dest.writeString(database);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PartyDocument> CREATOR = new Creator<PartyDocument>() {
        @Override
        public PartyDocument createFromParcel(Parcel in) {
            return new PartyDocument(in);
        }

        @Override
        public PartyDocument[] newArray(int size) {
            return new PartyDocument[size];
        }
    };

    public Debate getDebatt() {
        return debatt;
    }

    public DokIntressent getDokintressent() {
        return dokintressent;
    }

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

    public String getRm() {
        return rm;
    }

    public String getBeteckning() {
        return beteckning;
    }

    public boolean isMotion(){
        return dokumentnamn != null && dokumentnamn.equalsIgnoreCase("motion");
    }

    public String getDatum() {
        return datum;
    }

    public void setSaved(Long saved) {
        this.saved = saved;
    }

    public long getSaved() {
        return saved;
    }

    public String getDebattnamn() {
        return debattnamn;
    }

    public String getDebattdag() {
        return debattdag;
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public String toString() {
        return getTitel();
    }

    public PartyDocument(String publicerad, String titel) {
        this.publicerad = publicerad;
        this.titel = titel;
    }

    public long uniqueDocId(){
        String stringID = getId();
        stringID = stringID.replaceAll("[A-ö]+","");
        return Long.parseLong(stringID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartyDocument that = (PartyDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (titel != null ? !titel.equals(that.titel) : that.titel != null) return false;
        if (rm != null ? !rm.equals(that.rm) : that.rm != null) return false;
        if (beteckning != null ? !beteckning.equals(that.beteckning) : that.beteckning != null)
            return false;
        return doktyp != null ? doktyp.equals(that.doktyp) : that.doktyp == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (titel != null ? titel.hashCode() : 0);
        result = 31 * result + (rm != null ? rm.hashCode() : 0);
        result = 31 * result + (beteckning != null ? beteckning.hashCode() : 0);
        result = 31 * result + (doktyp != null ? doktyp.hashCode() : 0);
        return result;
    }

    public ArrayList<String> getSenders() {
        if (getDokintressent() == null) {
            return new ArrayList<String>();
        }
        ArrayList<String> senders = new ArrayList<>();
        for (Intressent i : getDokintressent().getIntressenter()) {
            if (i.getRoll().equals("undertecknare") || (getDoktyp().equals("frs") && i.getRoll().equals("besvaradav"))) {
                senders.add(i.getIntressent_id());
            }
        }
        return senders;
    }

}
