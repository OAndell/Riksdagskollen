package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Speech implements Parcelable {
    private String anforandetext;
    private String talare;

    public String getAnforandetext() {
        return anforandetext;
    }

    public String getTalare() {
        return talare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.anforandetext);
        dest.writeString(this.talare);
    }

    public Speech() {
    }

    protected Speech(Parcel in) {
        this.anforandetext = in.readString();
        this.talare = in.readString();
    }

    public static final Parcelable.Creator<Speech> CREATOR = new Parcelable.Creator<Speech>() {
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
