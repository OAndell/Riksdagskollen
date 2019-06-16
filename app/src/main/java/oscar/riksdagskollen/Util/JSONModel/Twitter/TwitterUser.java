package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class TwitterUser implements Parcelable {

    public static final Creator<TwitterUser> CREATOR = new Creator<TwitterUser>() {
        @Override
        public TwitterUser createFromParcel(Parcel source) {
            return new TwitterUser(source);
        }

        @Override
        public TwitterUser[] newArray(int size) {
            return new TwitterUser[size];
        }
    };
    private String name;
    private String screen_name;

    public TwitterUser() {
    }

    protected TwitterUser(Parcel in) {
        this.name = in.readString();
        this.screen_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }
}
