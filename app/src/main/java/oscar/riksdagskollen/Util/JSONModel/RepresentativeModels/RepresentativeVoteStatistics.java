package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oscar on 2018-09-29.
 */

public class RepresentativeVoteStatistics implements Parcelable {

    public static final Parcelable.Creator<RepresentativeVoteStatistics> CREATOR = new Parcelable.Creator<RepresentativeVoteStatistics>() {
        @Override
        public RepresentativeVoteStatistics createFromParcel(Parcel source) {
            return new RepresentativeVoteStatistics(source);
        }

        @Override
        public RepresentativeVoteStatistics[] newArray(int size) {
            return new RepresentativeVoteStatistics[size];
        }
    };
    @SerializedName("Ja")
    private String yes;
    @SerializedName("Nej")
    private String no;
    @SerializedName("Frånvarande")
    private String absent;
    @SerializedName("Avstår")
    private String abstained;

    protected RepresentativeVoteStatistics(Parcel in) {
        this.yes = in.readString();
        this.no = in.readString();
        this.absent = in.readString();
        this.abstained = in.readString();
    }

    public String getYes() {
        if (yes == null || yes.equals("")) {
            return "0";
        }
        return yes;
    }

    public String getNo() {
        if (no == null || no.equals("")) {
            return "0";
        }
        return no;
    }

    public String getAbsent() {
        if (absent == null || absent.equals("")) {
            return "0";
        }
        return absent;
    }

    public String getAbstained() {
        if (abstained == null || abstained.equals("")) {
            return "0";
        }
        return abstained;
    }

    public int getAttendancePercent() {
        float totalVotes = Integer.valueOf(getYes())
                + Integer.valueOf(getNo())
                + Integer.valueOf(getAbsent())
                + Integer.valueOf(getAbstained());
        float votesAttended = Integer.valueOf(getYes())
                + Integer.valueOf(getNo())
                + Integer.valueOf(getAbstained());
        return (int) ((votesAttended / totalVotes) * 100);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.yes);
        dest.writeString(this.no);
        dest.writeString(this.absent);
        dest.writeString(this.abstained);
    }
}
