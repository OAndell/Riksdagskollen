package oscar.riksdagskollen.Utilities.JSONModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionDocument implements Parcelable {

    private String dok_id;
    private String notisrubrik;
    private String publicerad;
    private String notis;
    private String beslutsdag;
    private String debattdag;
    private String justeringsdag;
    private String reservationer;
    private String typ;

    private String rm;
    private String beteckning;

    private boolean isExpanded = false;


    public String getDebattdag() {
        return debattdag;
    }

    public String getRm() {
        return rm;
    }

    public String getBeteckning() {
        return beteckning;
    }

    public String getJusteringsdag() {
        return justeringsdag;
    }

    public String getReservationer() {
        return reservationer;
    }

    public String getDok_id() {
        return dok_id;
    }

    public String getNotisrubrik() {
        return notisrubrik;
    }

    public String getPublicerad() {
        return publicerad;
    }

    public String getNotis() {
        return notis;
    }

    public String getBeslutsdag() {
        return beslutsdag;
    }

    public String getTyp() {
        return typ;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dok_id);
        dest.writeString(this.notisrubrik);
        dest.writeString(this.publicerad);
        dest.writeString(this.notis);
        dest.writeString(this.beslutsdag);
        dest.writeString(this.debattdag);
        dest.writeString(this.justeringsdag);
        dest.writeString(this.reservationer);
        dest.writeString(this.typ);
        dest.writeString(this.rm);
        dest.writeString(this.beteckning);
        dest.writeByte(this.isExpanded ? (byte) 1 : (byte) 0);
    }

    protected DecisionDocument(Parcel in) {
        this.dok_id = in.readString();
        this.notisrubrik = in.readString();
        this.publicerad = in.readString();
        this.notis = in.readString();
        this.beslutsdag = in.readString();
        this.debattdag = in.readString();
        this.justeringsdag = in.readString();
        this.reservationer = in.readString();
        this.typ = in.readString();
        this.rm = in.readString();
        this.beteckning = in.readString();
        this.isExpanded = in.readByte() != 0;
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