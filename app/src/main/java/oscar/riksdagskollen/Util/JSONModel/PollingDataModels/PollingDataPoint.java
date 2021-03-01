package oscar.riksdagskollen.Util.JSONModel.PollingDataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class PollingDataPoint implements Parcelable {

    private String period;
    private String percent;

    protected PollingDataPoint(Parcel in) {
        this.period = in.readString();
        this.percent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PollingDataPoint> CREATOR = new Creator<PollingDataPoint>() {
        @Override
        public PollingDataPoint createFromParcel(Parcel in) {
            return new PollingDataPoint(in);
        }

        @Override
        public PollingDataPoint[] newArray(int size) {
            return new PollingDataPoint[size];
        }
    };

    public String getPeriod() {
        return period;
    }

    public String getPercent() {
        return percent;
    }
}
