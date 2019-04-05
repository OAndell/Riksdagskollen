package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchedDocument extends java.lang.Object implements Parcelable {

    public static final Creator<SearchedDocument> CREATOR = new Creator<SearchedDocument>() {
        @Override
        public SearchedDocument createFromParcel(Parcel in) {
            return new SearchedDocument(in);
        }

        @Override
        public SearchedDocument[] newArray(int size) {
            return new SearchedDocument[size];
        }
    };
    private String id;

    protected SearchedDocument(Parcel in) {
        this.id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
