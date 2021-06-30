package oscar.riksdagskollen.Manager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CacheRequest;
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels.PartyData;
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels.PollingData;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;

public class RiksdagskollenAPIManager {

    private static final String HOST = "http://riksdagskollenapi-env-1.eba-chwg3wba.eu-west-1.elasticbeanstalk.com";
    private static final String PATH_POLLING = "/polling";
    private static final String PATH_PARTIES = "/parties";


    public RequestManager requestManager;

    private final Gson gson;

    public RiksdagskollenAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new Gson();
    }

    public void getPartyData(RKAPICallbacks.PartyDataListCallback callback) {
        requestManager.doStringGetRequest(HOST + PATH_PARTIES, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                PartyData[] partyData = gson.fromJson(response, PartyData[].class);
                callback.onFetched(partyData);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }

    public void getPartyData(String abbr, RKAPICallbacks.PartyDataCallback callback) {
        requestManager.doStringGetRequest(HOST + PATH_PARTIES + '/' + abbr, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                PartyData partyData = gson.fromJson(response, PartyData.class);
                callback.onFetched(partyData);
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }

    public void getPollingData(RKAPICallbacks.PollingDataListCallback callback) {
        requestManager.doStringGetRequest(HOST + PATH_POLLING, new StringRequestCallback() {
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

    public void getPollingDataForParty(String partyAbbreviation, final RKAPICallbacks.PollingDataCallback callback) {
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
