package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.PollingDataModels.PollingData;

public interface PollingDataListCallback {
    void onFetched(PollingData[] data);

    void onFail(VolleyError error);
}