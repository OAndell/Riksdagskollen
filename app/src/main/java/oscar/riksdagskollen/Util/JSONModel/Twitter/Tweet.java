package oscar.riksdagskollen.Util.JSONModel.Twitter;

import android.os.Parcel;
import android.os.Parcelable;

public class Tweet implements Parcelable {

    private long id;
    private String full_text;
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
    private String created_at;
    private Tweet retweeted_status;
    private TweetEntities entities;

    public long getId() {
        return id;
    }

    public String getText() {
        return full_text;
    }

    public Tweet() {

    }

    public String getImageUrl() {
        if (hasMedia()) {
            return getEntities().getMedia()[0].getMedia_url_https();
        } else return "";
    }


    protected Tweet(Parcel in) {
        this.id = in.readLong();
        this.full_text = in.readString();
        this.created_at = in.readString();
        this.retweeted_status = in.readParcelable(Tweet.class.getClassLoader());
        this.entities = in.readParcelable(TweetEntities.class.getClassLoader());
    }

    public TweetEntities getEntities() {
        return entities;
    }

    public Tweet getRetweeted_status() {
        return retweeted_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isRetweet() {
        return retweeted_status != null;
    }

    public boolean hasMedia() {
        if (entities != null) {
            return entities.getMedia() != null;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.full_text);
        dest.writeString(this.created_at);
        dest.writeParcelable(this.retweeted_status, flags);
        dest.writeParcelable(this.entities, flags);
    }
}
