package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class TweetURL implements Parcelable {

    private String url;
    private String expanded_url;
    private int[] indices;

    public String getUrl() {
        return url;
    }

    public String getExpanded_url() {
        return expanded_url;
    }

    public int[] getIndices() {
        return indices;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.expanded_url);
        dest.writeIntArray(this.indices);
    }

    protected TweetURL(Parcel in) {
        this.url = in.readString();
        this.expanded_url = in.readString();
        this.indices = in.createIntArray();
    }

    public static final Creator<TweetURL> CREATOR = new Creator<TweetURL>() {
        @Override
        public TweetURL createFromParcel(Parcel source) {
            return new TweetURL(source);
        }

        @Override
        public TweetURL[] newArray(int size) {
            return new TweetURL[size];
        }
    };
}
