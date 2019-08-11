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
        dest.writeString(this.getOrgan_kod());
        dest.writeString(this.getRoll_kod());
        dest.writeString(this.getStatus());
        dest.writeString(this.getTyp());
        dest.writeString(this.getFrom());
        dest.writeString(this.getTom());
    }

    public String getOrgan_kod() {
        return organ_kod;
    }

    public String getRoll_kod() {
        return roll_kod;
    }

    public String getStatus() {
        return status;
    }

    public String getTyp() {
        return typ;
    }

    public String getFrom() {
        return from;
    }

    public String getTom() {
        return tom;
    }
}
