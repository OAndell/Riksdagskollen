package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class Misson implements Parcelable {

    String organ_kod;
    String roll_kod;
    String status;
    String typ;
    String from;
    String tom;


    public Misson() {
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

    protected Misson(Parcel in) {
        this.organ_kod = in.readString();
        this.roll_kod = in.readString();
        this.status = in.readString();
        this.typ = in.readString();
        this.from = in.readString();
        this.tom = in.readString();
    }

    public static final Creator<Misson> CREATOR = new Creator<Misson>() {
        @Override
        public Misson createFromParcel(Parcel source) {
            return new Misson(source);
        }

        @Override
        public Misson[] newArray(int size) {
            return new Misson[size];
        }
    };
}
