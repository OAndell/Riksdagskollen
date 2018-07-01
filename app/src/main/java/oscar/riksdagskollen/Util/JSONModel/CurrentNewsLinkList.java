package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 2018-06-16.
 * To find url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks -> url
 */

public class CurrentNewsLinkList implements Parcelable {

    @SerializedName("link")
    private final CurrentNewsLink link;

    public CurrentNewsLink getLink(){
        return link;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.link, flags);
    }

    CurrentNewsLinkList(Parcel in) {
        this.link = in.readParcelable(CurrentNewsLink.class.getClassLoader());
    }

    public static final Parcelable.Creator<CurrentNewsLinkList> CREATOR = new Parcelable.Creator<CurrentNewsLinkList>() {
        @Override
        public CurrentNewsLinkList createFromParcel(Parcel source) {
            return new CurrentNewsLinkList(source);
        }

        @Override
        public CurrentNewsLinkList[] newArray(int size) {
            return new CurrentNewsLinkList[size];
        }
    };
}
