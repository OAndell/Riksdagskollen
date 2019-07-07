package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class TwitterUser implements Parcelable {

    private String name;
    private String screen_name;
    private String profile_image_url_https;


    public String getProfile_image_url_https() {
        return profile_image_url_https;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
        dest.writeString(this.profile_image_url_https);
    }

    protected TwitterUser(Parcel in) {
        this.name = in.readString();
        this.screen_name = in.readString();
        this.profile_image_url_https = in.readString();
    }

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
}
