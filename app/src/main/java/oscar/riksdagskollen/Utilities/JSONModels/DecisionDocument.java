package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionDocument implements Parcelable {

    private String id;
    private String notisrubrik;
    private String publicerad;
    private String notis;
    private String beslutsdag;
    private String typ;

    private boolean isExpanded = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotisrubrik() {
        return notisrubrik;
    }

    public void setNotisrubrik(String notisrubrik) {
        this.notisrubrik = notisrubrik;
    }

    public String getPublicerad() {
        return publicerad;
    }

    public void setPublicerad(String publicerad) {
        this.publicerad = publicerad;
    }

    public String getNotis() {
        return notis;
    }

    public void setNotis(String notis) {
        this.notis = notis;
    }

    public String getBeslutsdag() {
        return beslutsdag;
    }

    public void setBeslutsdag(String beslutsdag) {
        this.beslutsdag = beslutsdag;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public DecisionDocument() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.notisrubrik);
        dest.writeString(this.publicerad);
        dest.writeString(this.notis);
        dest.writeString(this.beslutsdag);
        dest.writeString(this.typ);
        dest.writeByte(this.isExpanded ? (byte) 1 : (byte) 0);
    }

    protected DecisionDocument(Parcel in) {
        this.id = in.readString();
        this.notisrubrik = in.readString();
        this.publicerad = in.readString();
        this.notis = in.readString();
        this.beslutsdag = in.readString();
        this.typ = in.readString();
        this.isExpanded = in.readByte() != 0;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public static final Creator<DecisionDocument> CREATOR = new Creator<DecisionDocument>() {
        @Override
        public DecisionDocument createFromParcel(Parcel source) {
            return new DecisionDocument(source);
        }

        @Override
        public DecisionDocument[] newArray(int size) {
            return new DecisionDocument[size];
        }
    };
}
