package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public class PersonalMisson implements Parcelable {

    private ArrayList<Misson> uppdrag;

    public ArrayList<Misson> getUppdrag() {
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

    public PersonalMisson() {
    }

    protected PersonalMisson(Parcel in) {
        this.uppdrag = in.createTypedArrayList(Misson.CREATOR);
    }

    public static final Parcelable.Creator<PersonalMisson> CREATOR = new Parcelable.Creator<PersonalMisson>() {
        @Override
        public PersonalMisson createFromParcel(Parcel source) {
            return new PersonalMisson(source);
        }

        @Override
        public PersonalMisson[] newArray(int size) {
            return new PersonalMisson[size];
        }
    };
}
