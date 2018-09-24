package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by oscar on 2018-09-24.
 */

public class RepresentativeInfoList implements Parcelable {

    public static final Creator<RepresentativeInfoList> CREATOR = new Creator<RepresentativeInfoList>() {
        @Override
        public RepresentativeInfoList createFromParcel(Parcel in) {
            return new RepresentativeInfoList(in);
        }

        @Override
        public RepresentativeInfoList[] newArray(int size) {
            return new RepresentativeInfoList[size];
        }
    };
    private ArrayList<RepresentativeInfo> uppgift;

    protected RepresentativeInfoList(Parcel in) {
        this.uppgift = in.createTypedArrayList(RepresentativeInfo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.uppgift);
    }

    public ArrayList<RepresentativeInfo> getUppgift() {
        return uppgift;
    }
}
