package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class RepresentativeMission implements Parcelable {

    public static final Creator<RepresentativeMission> CREATOR = new Creator<RepresentativeMission>() {
        @Override
        public RepresentativeMission createFromParcel(Parcel source) {
            return new RepresentativeMission(source);
        }

        @Override
        public RepresentativeMission[] newArray(int size) {
            return new RepresentativeMission[size];
        }
    };
    String organ_kod;
    String roll_kod;
    String status;
    String typ;
    String from;
    String tom;

    public RepresentativeMission() {
    }

    protected RepresentativeMission(Parcel in) {
        this.organ_kod = in.readString();
        this.roll_kod = in.readString();
        this.status = in.readString();
        this.typ = in.readString();
        this.from = in.readString();
        this.tom = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.organ_kod);
        dest.writeString(this.roll_kod);
        dest.writeString(this.status);
        dest.writeString(this.typ);
        dest.writeString(this.from);
        dest.writeString(this.tom);
    }
}
