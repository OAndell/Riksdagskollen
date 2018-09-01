package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

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
        return dokumentnamn.equalsIgnoreCase("motion");
    }

    public String getDatum() {
        return datum;
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
    public int describeContents() {
        return 0;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.undertitel);
        dest.writeString(this.titel);
        dest.writeString(this.rm);
        dest.writeString(this.typ);
        dest.writeString(this.beteckning);
        dest.writeString(this.publicerad);
        dest.writeString(this.doktyp);
        dest.writeString(this.dokument_url_text);
        dest.writeString(this.dokument_url_html);
        dest.writeString(this.traff);
        dest.writeString(this.summary);
        dest.writeString(this.dokumentnamn);
        dest.writeString(this.datum);
        dest.writeParcelable(this.dokintressent, flags);
    }

    protected PartyDocument(Parcel in) {
        this.id = in.readString();
        this.undertitel = in.readString();
        this.titel = in.readString();
        this.rm = in.readString();
        this.typ = in.readString();
        this.beteckning = in.readString();
        this.publicerad = in.readString();
        this.doktyp = in.readString();
        this.dokument_url_text = in.readString();
        this.dokument_url_html = in.readString();
        this.traff = in.readString();
        this.summary = in.readString();
        this.dokumentnamn = in.readString();
        this.datum = in.readString();
        this.dokintressent = in.readParcelable(DokIntressent.class.getClassLoader());
    }

    public static final Creator<PartyDocument> CREATOR = new Creator<PartyDocument>() {
        @Override
        public PartyDocument createFromParcel(Parcel source) {
            return new PartyDocument(source);
        }

        @Override
        public PartyDocument[] newArray(int size) {
            return new PartyDocument[size];
        }
    };
}
