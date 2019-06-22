package oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-03-28.
 * To find real url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks
 */

public class CurrentNews implements Parcelable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentNews that = (CurrentNews) o;

        if (!id.equals(that.id)) return false;
        if (!titel.equals(that.titel)) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return linklista != null ? linklista.equals(that.linklista) : that.linklista == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + titel.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (linklista != null ? linklista.hashCode() : 0);
        return result;
    }

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

    private CurrentNewsLinkList getLinklista() {
       return linklista;
    }

    public String getUrl() {
        return url;
    }

    //Get the real URL
    public String getNewsUrl() {
        try {
            String url = getLinklista().getLink().getUrl();
            if (url.startsWith("http")) {
                return url;
            } else {
                return "https://riksdagen.se" + url;
            }
        }
        // Some news does not contain the LinkLista object
        catch (NullPointerException e) {
            String url = getUrl();
            if (url.startsWith("http")) {
                return url;
            } else {
                return "https://riksdagen.se" + url;
            }
        }

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
