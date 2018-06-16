package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-06-16.
 */

public class Protocol implements Parcelable {

    private String dokument_url_text;

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

    private String dokument_url_html;
    private String titel;
    private String id;

    protected Protocol(Parcel in) {
        id = in.readString();
        titel = in.readString();
        dokument_url_text = in.readString();
        dokument_url_html = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.titel);
        dest.writeString(this.dokument_url_text);
        dest.writeString(this.dokument_url_html);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Protocol> CREATOR = new Creator<Protocol>() {
        @Override
        public Protocol createFromParcel(Parcel in) {
            return new Protocol(in);
        }

        @Override
        public Protocol[] newArray(int size) {
            return new Protocol[size];
        }
    };
}
