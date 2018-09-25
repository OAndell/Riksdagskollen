package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class RepresentativeMissionList implements Parcelable {

    public static final Parcelable.Creator<RepresentativeMissionList> CREATOR = new Parcelable.Creator<RepresentativeMissionList>() {
        @Override
        public RepresentativeMissionList createFromParcel(Parcel source) {
            return new RepresentativeMissionList(source);
        }

        @Override
        public RepresentativeMissionList[] newArray(int size) {
            return new RepresentativeMissionList[size];
        }
    };
    private ArrayList<RepresentativeMission> uppdrag;

    public RepresentativeMissionList() {
    }

    protected RepresentativeMissionList(Parcel in) {
        this.uppdrag = in.createTypedArrayList(RepresentativeMission.CREATOR);
    }

    public ArrayList<RepresentativeMission> getUppdrag() {
        return uppdrag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.uppdrag);
    }
}
