package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Debate implements Parcelable {


    private DebateSpeech[] anforande;

    public DebateSpeech[] getAnforande() {
        return anforande;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.anforande, flags);
    }

    public Debate() {
    }

    protected Debate(Parcel in) {
        this.anforande = in.createTypedArray(DebateSpeech.CREATOR);
    }

    public static final Parcelable.Creator<Debate> CREATOR = new Parcelable.Creator<Debate>() {
        @Override
        public Debate createFromParcel(Parcel source) {
            return new Debate(source);
        }

        @Override
        public Debate[] newArray(int size) {
            return new Debate[size];
        }
    };
}
