package oscar.riksdagskollen.Util.JSONModel.RikdagsKollenModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-10-01.
 */

public class User implements Parcelable {

    private String id;
    private String party;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.party);
    }

    public User(String id, String party) {
        this.id = id;
        this.party = party;
    }

    public String getId() {
        return id;
    }

    public String getParty() {
        return party;
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.party = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "USER: id=" + id + " party=" + party;
    }
}
