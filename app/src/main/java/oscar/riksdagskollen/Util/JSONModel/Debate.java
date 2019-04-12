package oscar.riksdagskollen.Util.JSONModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import oscar.riksdagskollen.Util.Enum.CurrentParties;

public class Debate implements Parcelable {


    private DebateSpeech[] anforande;

    public DebateSpeech[] getAnforande() {
        return anforande;
    }

    public ArrayList<Party> getPartiesInDebate() {
        ArrayList<Party> parties = new ArrayList<>();
        for (int i = 0; i < anforande.length; i++) {
            Party party = CurrentParties.getParty(anforande[i].getParti());
            if (!parties.contains(party)) {
                parties.add(party);
            }
        }
        return parties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.anforande, flags);
    }



    protected Debate(Parcel in) {
        System.out.println(in.toString());
        this.anforande = in.createTypedArray(DebateSpeech.CREATOR);
    }

    public static final Parcelable.Creator<Debate> CREATOR = new Parcelable.Creator<Debate>() {
        @Override
        public Debate createFromParcel(Parcel source) {
            return new Debate(source);
        }

        @Override
        public Debate[] newArray(int size) {
            return new Debate[size];
        }
    };


    //Because of shitty API
    public static class DebateDezerializer implements JsonDeserializer<Debate> {
        @Override
        public Debate deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObj = json.getAsJsonObject();
            JsonElement speeches = jsonObj.get("anforande");
            if (speeches.isJsonArray()) {
                return new Gson().fromJson(json, Debate.class);
            } else {
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(speeches);
                jsonObj.remove("anforande");
                jsonObj.add("anforande", jsonArray);
                return new Gson().fromJson(jsonObj, Debate.class);
            }
        }

    }
}
