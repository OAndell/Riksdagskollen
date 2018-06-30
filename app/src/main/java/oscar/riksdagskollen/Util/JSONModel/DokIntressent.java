package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by gustavaaro on 2018-03-28.
 */

public class DokIntressent implements Parcelable {

    private ArrayList<Intressent> intressent;

    public ArrayList<Intressent> getIntressenter() {
        return intressent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.intressent);
    }

    public DokIntressent() {
    }

    protected DokIntressent(Parcel in) {
        this.intressent = in.createTypedArrayList(Intressent.CREATOR);
    }

    public static final Parcelable.Creator<DokIntressent> CREATOR = new Parcelable.Creator<DokIntressent>() {
        @Override
        public DokIntressent createFromParcel(Parcel source) {
            return new DokIntressent(source);
        }

        @Override
        public DokIntressent[] newArray(int size) {
            return new DokIntressent[size];
        }
    };
}
