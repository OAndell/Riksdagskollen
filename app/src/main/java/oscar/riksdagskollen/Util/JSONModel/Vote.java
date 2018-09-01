package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by oscar on 2018-06-16.
 */

public class Vote implements Parcelable {

    public String getDatum(){
        return datum;
    }

    public String getId() {
        return id;
    }

    public String getPublicerad() {
        return publicerad;
    }

    public String getUndertitel() {
        return undertitel;
    }

    public String getDokument_url_text() {
        return dokument_url_text;
    }

    public String getDokument_url_html() {
        return dokument_url_html;
    }

    public String getTitel() {
        return titel;
    }

    public String getSummary() {
        return summary;
    }

    public String getNotis() {
        return notis;
    }

    public String getBeteckning() {
        return beteckning;
    }

    private String datum;
    private String id;
    private String publicerad;
    private String undertitel;
    private String dokument_url_text;
    private String dokument_url_html;
    private String titel;
    private String summary;
    private String beteckning;
    private String notis;
    private HashMap<String, int[]> voteResults;

    public void setVoteResults(HashMap<String, int[]> voteResults) {
        this.voteResults = voteResults;
    }

    public HashMap<String, int[]> getVoteResults() {
        return voteResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.datum);
        dest.writeString(this.id);
        dest.writeString(this.publicerad);
        dest.writeString(this.undertitel);
        dest.writeString(this.dokument_url_text);
        dest.writeString(this.dokument_url_html);
        dest.writeString(this.titel);
        dest.writeString(this.summary);
        dest.writeString(this.beteckning);
        dest.writeString(this.notis);
        dest.writeSerializable(this.voteResults);
    }

    public Vote() {
    }

    protected Vote(Parcel in) {
        this.datum = in.readString();
        this.id = in.readString();
        this.publicerad = in.readString();
        this.undertitel = in.readString();
        this.dokument_url_text = in.readString();
        this.dokument_url_html = in.readString();
        this.titel = in.readString();
        this.summary = in.readString();
        this.beteckning = in.readString();
        this.notis = in.readString();
        this.voteResults = (HashMap<String, int[]>) in.readSerializable();
    }

    public static final Creator<Vote> CREATOR = new Creator<Vote>() {
        @Override
        public Vote createFromParcel(Parcel source) {
            return new Vote(source);
        }

        @Override
        public Vote[] newArray(int size) {
            return new Vote[size];
        }
    };
}
