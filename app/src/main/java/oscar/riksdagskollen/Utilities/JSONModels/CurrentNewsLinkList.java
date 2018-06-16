package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-06-16.
 * To find url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks -> url
 */

public class CurrentNewsLinkList implements Parcelable {

    private CurrentNewsLink link;

    protected CurrentNewsLinkList(Parcel in) {
        this.link = in.readParcelable(CurrentNewsLink[].class.getClassLoader());

    }

    public CurrentNewsLink getLink(){
        return link;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.link, flags);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrentNewsLinkList> CREATOR = new Creator<CurrentNewsLinkList>() {
        @Override
        public CurrentNewsLinkList createFromParcel(Parcel in) {
            return new CurrentNewsLinkList(in);
        }

        @Override
        public CurrentNewsLinkList[] newArray(int size) {
            return new CurrentNewsLinkList[size];
        }
    };
}
