package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class TweetEntities implements Parcelable {

    private TweetMedia[] media;
    private TweetURL[] urls;

    public TweetURL[] getUrls() {
        return urls;
    }

    public TweetMedia[] getMedia() {
        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.media, flags);
        dest.writeTypedArray(this.urls, flags);
    }

    protected TweetEntities(Parcel in) {
        this.media = in.createTypedArray(TweetMedia.CREATOR);
        this.urls = in.createTypedArray(TweetURL.CREATOR);
    }

    public static final Creator<TweetEntities> CREATOR = new Creator<TweetEntities>() {
        @Override
        public TweetEntities createFromParcel(Parcel source) {
            return new TweetEntities(source);
        }

        @Override
        public TweetEntities[] newArray(int size) {
            return new TweetEntities[size];
        }
    };
}
