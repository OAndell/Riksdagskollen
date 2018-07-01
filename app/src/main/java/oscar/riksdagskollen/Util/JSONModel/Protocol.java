package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-06-16.
 */

public class Protocol implements Parcelable {

    private final String dokument_url_text;

    public String getDokument_url_text() {
        return dokument_url_text;
    }

    public String getDokument_url_html() {
        return dokument_url_html;
    }

    public String getTitel() {
        return titel;
    }

    public String getId() {
        return id;
    }

    private final String dokument_url_html;
    private final String titel;
    private final String id;
    private final String datum;
    private final String summary;

    public String getDatum() {
        return datum;
    }

    public String getSummary() {
        return summary;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dokument_url_text);
        dest.writeString(this.dokument_url_html);
        dest.writeString(this.titel);
        dest.writeString(this.id);
        dest.writeString(this.datum);
        dest.writeString(this.summary);
    }

    private Protocol(Parcel in) {
        this.dokument_url_text = in.readString();
        this.dokument_url_html = in.readString();
        this.titel = in.readString();
        this.id = in.readString();
        this.datum = in.readString();
        this.summary = in.readString();
    }

    public static final Creator<Protocol> CREATOR = new Creator<Protocol>() {
        @Override
        public Protocol createFromParcel(Parcel source) {
            return new Protocol(source);
        }

        @Override
        public Protocol[] newArray(int size) {
            return new Protocol[size];
        }
    };
}
