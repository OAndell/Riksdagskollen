package oscar.riksdagskollen.Util.JSONModel.RepresentativeModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

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
    private String[] uppgift;
    private String typ;
    private String intressent_id;
    private String hangar_id;
    RepresentativeInfo() {

    }

    protected RepresentativeInfo(Parcel in) {
        this.kod = in.readString();
        this.uppgift = in.createStringArray();
        this.typ = in.readString();
        this.intressent_id = in.readString();
        this.hangar_id = in.readString();
    }

    public String[] getUppgift() {
        return uppgift;
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

    public static class RepresentativeInfoDezerializer implements JsonDeserializer<RepresentativeInfo> {

        @Override
        public RepresentativeInfo deserialize(JsonElement json, Type typeOfT,
                                              JsonDeserializationContext context) throws JsonParseException {

            RepresentativeInfo representativeInfo = new RepresentativeInfo();

            // Fix faulty json where uppgift:[""] sometimes would be uppgift:[{}]
            try {
                representativeInfo = new Gson().fromJson(json, RepresentativeInfo.class);
            } catch (JsonSyntaxException e) {
                try {
                    JSONObject tmp = new JSONObject(json.toString());
                    tmp.remove("uppgift");
                    JSONArray emptyArray = new JSONArray();
                    emptyArray.put("");
                    tmp.put("uppgift", emptyArray);
                    representativeInfo = new Gson().fromJson(tmp.toString(), RepresentativeInfo.class);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            return representativeInfo;
        }

    }

}
