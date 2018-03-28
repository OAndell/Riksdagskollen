package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-03-28.
 */

public class Intressent implements Parcelable {

    String roll;
    String namn;
    String intressent_id;
    String partibet;

    public String getRoll() {
        return roll;
    }

    public String getNamn() {
        return namn;
    }

    public String getIntressent_id() {
        return intressent_id;
    }

    public String getPartibet() {
        return partibet;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roll);
        dest.writeString(this.namn);
        dest.writeString(this.intressent_id);
        dest.writeString(this.partibet);
    }

    public Intressent() {
    }

    protected Intressent(Parcel in) {
        this.roll = in.readString();
        this.namn = in.readString();
        this.intressent_id = in.readString();
        this.partibet = in.readString();
    }

    public static final Parcelable.Creator<Intressent> CREATOR = new Parcelable.Creator<Intressent>() {
        @Override
        public Intressent createFromParcel(Parcel source) {
            return new Intressent(source);
        }

        @Override
        public Intressent[] newArray(int size) {
            return new Intressent[size];
        }
    };
}
