package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by oscar on 2018-09-24.
 */

public class RepresentativeInfoList implements Parcelable {

    public static final Creator<RepresentativeInfoList> CREATOR = new Creator<RepresentativeInfoList>() {
        @Override
        public RepresentativeInfoList createFromParcel(Parcel in) {
            return new RepresentativeInfoList(in);
        }

        @Override
        public RepresentativeInfoList[] newArray(int size) {
            return new RepresentativeInfoList[size];
        }
    };
    private ArrayList<RepresentativeInfo> uppgift;

    protected RepresentativeInfoList() {
        this.uppgift = new ArrayList<>();
    }

    protected RepresentativeInfoList(Parcel in) {
        this.uppgift = in.createTypedArrayList(RepresentativeInfo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.uppgift);
    }

    public ArrayList<RepresentativeInfo> getUppgift() {
        return uppgift;
    }

    public static class RepresentativeInfoListDeserializer implements JsonDeserializer<RepresentativeInfoList> {

        @Override
        public RepresentativeInfoList deserialize(JsonElement json, Type typeOfT,
                                                  JsonDeserializationContext context) throws JsonParseException {

            if (json instanceof JsonObject) {
                return new Gson().fromJson(json, RepresentativeInfoList.class);
            }
            return new RepresentativeInfoList();
        }

    }
}
