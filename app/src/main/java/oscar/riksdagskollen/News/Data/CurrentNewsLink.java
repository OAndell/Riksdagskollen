package oscar.riksdagskollen.News.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by oscar on 2018-06-16.
 * To find url:
 * CurrentNews -> CurrentNewsLinkList -> CurrentNewsLinks
 */

public class CurrentNewsLink  implements Parcelable {
    public static final Creator<CurrentNewsLink> CREATOR = new Creator<CurrentNewsLink>() {
        @Override
        public CurrentNewsLink createFromParcel(Parcel in) {
            return new CurrentNewsLink(in);
        }

        @Override
        public CurrentNewsLink[] newArray(int size) {
            return new CurrentNewsLink[size];
        }
    };
    private final String url;

    protected CurrentNewsLink(Parcel in) {
        url = in.readString();

    }

    public String getUrl(){
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
    }

    public static class CurrentNewsLinkDeserializer implements JsonDeserializer<CurrentNewsLink> {

        @Override
        public CurrentNewsLink deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

            if (json instanceof JsonArray) {
                return new Gson().fromJson(json, CurrentNewsLink[].class)[0];
            }

            return new Gson().fromJson(json, CurrentNewsLink.class);
        }

    }
}
