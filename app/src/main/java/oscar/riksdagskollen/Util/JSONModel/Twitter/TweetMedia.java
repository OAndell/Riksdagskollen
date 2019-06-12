package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class TweetMedia implements Parcelable {

    public static final Creator<TweetMedia> CREATOR = new Creator<TweetMedia>() {
        @Override
        public TweetMedia createFromParcel(Parcel source) {
            return new TweetMedia(source);
        }

        @Override
        public TweetMedia[] newArray(int size) {
            return new TweetMedia[size];
        }
    };
    private String display_url;
    private String media_url_https;

    public TweetMedia() {
    }

    protected TweetMedia(Parcel in) {
        this.display_url = in.readString();
        this.media_url_https = in.readString();
    }

    public String getDisplay_url() {
        return display_url;
    }

    public String getMedia_url_https() {
        return media_url_https;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.display_url);
        dest.writeString(this.media_url_https);
    }
}
