package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oscar on 2018-09-24.
 */

public class RepresentativeInfo implements Parcelable {

    public static final Creator<RepresentativeInfo> CREATOR = new Creator<RepresentativeInfo>() {
        @Override
        public RepresentativeInfo createFromParcel(Parcel in) {
            return new RepresentativeInfo(in);
        }

        @Override
        public RepresentativeInfo[] newArray(int size) {
            return new RepresentativeInfo[size];
        }
    };
    private String kod;
    //private String[] uppgift;
    private String typ;

    /*public String[] getUppgift() {
        return uppgift;
    }*/
    private String intressent_id;
    private String hangar_id;

    protected RepresentativeInfo(Parcel in) {
        this.kod = in.readString();
        //this.uppgift = in.createStringArray();
        this.typ = in.readString();
        this.intressent_id = in.readString();
        this.hangar_id = in.readString();
    }

    public String getKod() {
        return kod;
    }

    public String getTyp() {
        return typ;
    }

    public String getIntressent_id() {
        return intressent_id;
    }

    public String getHangar_id() {
        return hangar_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kod);
        //dest.writeStringArray(this.uppgift);
        dest.writeString(this.typ);
        dest.writeString(this.intressent_id);
        dest.writeString(this.hangar_id);
    }

}
