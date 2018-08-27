package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by gustavaaro on 2018-03-25.
 */

public class Party implements Parcelable {

    private final String id;
    private final String name;
    private final int drawableLogo;

    public Party(String name, String id, int drawableLogo) {
        this.name = name;
        this.id = id;
        this.drawableLogo = drawableLogo;
    }

    public String getID(){
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDrawableLogo(){
        return drawableLogo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.drawableLogo);
    }

    private Party(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.drawableLogo = in.readInt();
    }

    public static final Parcelable.Creator<Party> CREATOR = new Parcelable.Creator<Party>() {
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