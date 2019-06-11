package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable {

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
    private int id;
    private String text;

    protected Tweet(Parcel in) {
        this.id = id;
        this.text = text;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
