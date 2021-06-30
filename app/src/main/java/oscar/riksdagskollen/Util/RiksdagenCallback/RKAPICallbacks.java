package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels.PartyData;
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels.PollingData;

public class RKAPICallbacks {

    public interface PartyDataCallback {
        void onFetched(PartyData data);

        void onFail(VolleyError error);
    }

    public interface PartyDataListCallback {
        void onFetched(PartyData[] data);

        void onFail(VolleyError error);
    }

    public interface PollingDataCallback {
        void onFetched(PollingData data);

        void onFail(VolleyError error);
    }

    public interface PollingDataListCallback {
        void onFetched(PollingData[] data);

        void onFail(VolleyError error);
    }

}