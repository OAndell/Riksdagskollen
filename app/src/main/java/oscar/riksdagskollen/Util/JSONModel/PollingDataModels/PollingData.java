package oscar.riksdagskollen.Util.JSONModel.PollingDataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PollingData implements Parcelable {

    private ArrayList<PollingDataPoint> data;
    private String party;
    private String source;

    protected PollingData(Parcel in) {
        System.out.println(in.toString());
        this.party = in.readString();
        this.data = in.createTypedArrayList(PollingDataPoint.CREATOR);
        this.source = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PollingData> CREATOR = new Creator<PollingData>() {
        @Override
        public PollingData createFromParcel(Parcel in) {
            return new PollingData(in);
        }

        @Override
        public PollingData[] newArray(int size) {
            return new PollingData[size];
        }
    };

    public ArrayList<PollingDataPoint> getDataPoints() {
        return data;
    }

    public String getParty() {
        return party;
    }

    public String getSource() {
        return source;
    }
}
