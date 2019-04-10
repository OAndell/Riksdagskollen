package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Speech implements Parcelable {
    private String anforandetext;
    private String talare;
    private String systemnyckel;

    public String getAnforandetext() {
        return anforandetext;
    }

    public String getTalare() {
        return talare;
    }

    public Speech() {
    }

    public String getSystemnyckel() {
        return systemnyckel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.anforandetext);
        dest.writeString(this.talare);
        dest.writeString(this.systemnyckel);
    }

    protected Speech(Parcel in) {
        this.anforandetext = in.readString();
        this.talare = in.readString();
        this.systemnyckel = in.readString();
    }

    public static final Creator<Speech> CREATOR = new Creator<Speech>() {
        @Override
        public Speech createFromParcel(Parcel source) {
            return new Speech(source);
        }

        @Override
        public Speech[] newArray(int size) {
            return new Speech[size];
        }
    };
}
