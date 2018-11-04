package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by gustavaaro on 2018-03-25.
 */

public class Party implements Parcelable {

    private final String id;
    private final String name;
    private final String website;
    private final String ideology;
    private final int drawableLogo;
    private final String wikiUrl;

    public Party(String name, String id, int drawableLogo, String website, String ideology, String wikiUrl) {
        this.name = name;
        this.wikiUrl = wikiUrl;
        this.id = id;
        this.drawableLogo = drawableLogo;
        this.ideology = ideology;
        this.website = website;
    }

    public String getID(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsite(){
        return website;
    }

    public int getDrawableLogo(){
        return drawableLogo;
    }

    public String getIdeology(){
        return ideology;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (!id.equals(party.id)) return false;
        return name.equals(party.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.website);
        dest.writeString(this.ideology);
        dest.writeInt(this.drawableLogo);
        dest.writeString(this.wikiUrl);
    }

    protected Party(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.website = in.readString();
        this.ideology = in.readString();
        this.drawableLogo = in.readInt();
        this.wikiUrl = in.readString();
    }

    public static final Creator<Party> CREATOR = new Creator<Party>() {
        @Override
        public Party createFromParcel(Parcel source) {
            return new Party(source);
        }

        @Override
        public Party[] newArray(int size) {
            return new Party[size];
        }
    };
}