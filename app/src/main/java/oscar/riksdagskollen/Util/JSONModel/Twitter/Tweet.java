package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable {

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
    private long id;
    private String full_text;


    @Override
    public int describeContents() {
        return 0;
    }

    private boolean truncated;

    protected Tweet(Parcel in) {
        this.id = in.readLong();
        this.full_text = in.readString();
        this.truncated = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.full_text);
        dest.writeByte(this.truncated ? (byte) 1 : (byte) 0);
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return full_text;
    }

    public boolean isTruncated() {
        return truncated;
    }
}
