package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-06-16.
 * To find url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks
 */

public class CurrentNewsLink  implements Parcelable {
    private String url;

    protected CurrentNewsLink(Parcel in) {
        url = in.readString();
    }

    public static final Creator<CurrentNews> CREATOR = new Creator<CurrentNews>() {
        @Override
        public CurrentNews createFromParcel(Parcel in) {
            return new CurrentNews(in);
        }

        @Override
        public CurrentNews[] newArray(int size) {
            return new CurrentNews[size];
        }
    };

    public String getUrl(){
        return url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
    }
}
