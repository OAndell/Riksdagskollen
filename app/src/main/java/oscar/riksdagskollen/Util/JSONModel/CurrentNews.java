package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-03-28.
 * To find real url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks
 */

public class CurrentNews implements Parcelable {
    private final String id;
    private final String titel;
    private final String publicerad;
    private final String summary;
    private final String url; //This is often not the correct url
    //use CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks -> url
    private final String img_url;
    private final String img_text;
    private final String img_fotograf;
    private final String img_tumnagel_url;
    private final CurrentNewsLinkList linklista;

    private CurrentNews(Parcel in) {
        id = in.readString();
        titel = in.readString();
        publicerad = in.readString();
        summary = in.readString();
        url = in.readString();
        img_url = in.readString();
        img_text = in.readString();
        img_fotograf = in.readString();
        img_tumnagel_url = in.readString();
        this.linklista = in.readParcelable(CurrentNewsLinkList.class.getClassLoader());

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

    public String getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getSummary() {
        return summary;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getImg_text() {
        return img_text;
    }

    public String getImg_fotograf() {
        return img_fotograf;
    }

    public String getImg_tumnagel_url() {
        return img_tumnagel_url;
    }

    public CurrentNewsLinkList getLinklista(){
       return linklista;
    }

    public String getUrl(){
        return url;
    }

    public String getPublicerad() {
        return publicerad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.id);
        dest.writeString(this.titel);
        dest.writeString(this.publicerad);
        dest.writeString(this.url);
        dest.writeString(this.summary);
        dest.writeParcelable(this.linklista, i);
    }
}
