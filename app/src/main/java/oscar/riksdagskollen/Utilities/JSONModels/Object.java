package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class Object extends java.lang.Object implements Parcelable {

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
    DokIntressent dokintressent;

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

    public boolean isMotion(){
        return dokumentnamn.equalsIgnoreCase("motion");
    }

    @Override
    public String toString() {
        return getTitel();
    }

    public Object(String publicerad, String titel) {
        this.publicerad = publicerad;
        this.titel = titel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.undertitel);
        dest.writeString(this.titel);
        dest.writeString(this.typ);
        dest.writeString(this.publicerad);
        dest.writeString(this.doktyp);
        dest.writeString(this.dokument_url_text);
        dest.writeString(this.dokument_url_html);
        dest.writeString(this.traff);
        dest.writeString(this.summary);
        dest.writeString(this.dokumentnamn);
        dest.writeParcelable(this.dokintressent, flags);
    }

    protected Object(Parcel in) {
        this.id = in.readString();
        this.undertitel = in.readString();
        this.titel = in.readString();
        this.typ = in.readString();
        this.publicerad = in.readString();
        this.doktyp = in.readString();
        this.dokument_url_text = in.readString();
        this.dokument_url_html = in.readString();
        this.traff = in.readString();
        this.summary = in.readString();
        this.dokumentnamn = in.readString();
        this.dokintressent = in.readParcelable(DokIntressent.class.getClassLoader());
    }

    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Object(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };
}
