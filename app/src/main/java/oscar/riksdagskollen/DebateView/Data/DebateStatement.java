package oscar.riksdagskollen.DebateView.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class DebateStatement implements Parcelable {

    private String parti;
    private String anf_nummer;
    private String anf_datumtid;
    private String intressent_id;
    private String talare;
    private String anf_klockslag;
    private String tumnagel;
    private String video_url;
    private String anf_sekunder;
    private Speech speech;
    private String webTVUrl;

    public String getWebTVUrl() {
        return webTVUrl;
    }

    public void setWebTVUrl(String webTVUrl) {
        this.webTVUrl = webTVUrl;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getAnf_sekunder() {
        return anf_sekunder;
    }

    public String getTumnagel() {
        return tumnagel;
    }

    public String getParti() {
        return parti;
    }

    public String getAnf_nummer() {
        return anf_nummer;
    }

    public String getAnf_datumtid() {
        return anf_datumtid;
    }

    public String getIntressent_id() {
        return intressent_id;
    }

    public String getTalare() {
        return talare;
    }

    public String getAnf_klockslag() {
        return anf_klockslag;
    }

    public DebateStatement() {
    }

    public void setSpeech(Speech speech) {
        this.speech = speech;
    }

    public Speech getSpeech() {
        return speech;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.parti);
        dest.writeString(this.anf_nummer);
        dest.writeString(this.anf_datumtid);
        dest.writeString(this.intressent_id);
        dest.writeString(this.talare);
        dest.writeString(this.anf_klockslag);
        dest.writeString(this.tumnagel);
        dest.writeString(this.video_url);
        dest.writeString(this.anf_sekunder);
        dest.writeParcelable(this.speech, flags);
        dest.writeString(this.webTVUrl);
    }

    protected DebateStatement(Parcel in) {
        this.parti = in.readString();
        this.anf_nummer = in.readString();
        this.anf_datumtid = in.readString();
        this.intressent_id = in.readString();
        this.talare = in.readString();
        this.anf_klockslag = in.readString();
        this.tumnagel = in.readString();
        this.video_url = in.readString();
        this.anf_sekunder = in.readString();
        this.speech = in.readParcelable(Speech.class.getClassLoader());
        this.webTVUrl = in.readString();
    }

    public static final Creator<DebateStatement> CREATOR = new Creator<DebateStatement>() {
        @Override
        public DebateStatement createFromParcel(Parcel source) {
            return new DebateStatement(source);
        }

        @Override
        public DebateStatement[] newArray(int size) {
            return new DebateStatement[size];
        }
    };
}
