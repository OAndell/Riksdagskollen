package oscar.riksdagskollen.Manager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CacheRequest;
import oscar.riksdagskollen.Util.JSONModel.PollingDataModels.PollingData;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.PollingDataCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.PollingDataListCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;

public class RiksdagskollenAPIManager {

    private static final String HOST = "http://riksdagskollenapi-env-1.eba-chwg3wba.eu-west-1.elasticbeanstalk.com";
    private static final String PATH_POLLING = "/polling";

    public RequestManager requestManager;

    private final Gson gson;

    public RiksdagskollenAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new Gson();
    }

    public void getPollingData(PollingDataListCallback callback) {
        requestManager.doStringGetRequest(HOST+ PATH_POLLING, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                PollingData[] pollingData = gson.fromJson(response, PollingData[].class);
                callback.onFetched(pollingData);
            }

            @Override
            public void onFail(VolleyError error) {
                System.out.println(error);
            }
        });
    }


    public void getPollingDataForParty( String partyAbbreviation, final PollingDataCallback callback) {
        String path = PATH_POLLING + "/" + partyAbbreviation;
        requestManager.doCachedGetRequest(path, HOST, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                System.out.println(response.toString());
                PollingData pollingData = gson.fromJson(response.toString(), PollingData.class);
                callback.onFetched(pollingData);
            }

            @Override
            public void onRequestFail(VolleyError error) {

            }
        });
    }
}
