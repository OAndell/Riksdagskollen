package oscar.riksdagskollen.Manager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

public class TwitterAPIManager {

    private static final String HOST = "https://api.twitter.com/";
    private static final String AUTH_ENDPOINT = "oauth2/token?grant_type=client_credentials";
    private RequestManager requestManager;
    private Gson gson;
    private String bearerToken;
    private String apiKey = "";
    private boolean hasAuth = false;


    public TwitterAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new Gson();
    }

    public TwitterAPIManager getInstance() {
        return RiksdagskollenApp.getInstance().getTwitterAPIManager();
    }

    public void getTweets(String screenName, final TwitterCallback callback) {

    }

    public void Authenticate() {
        requestManager.doTwitterAuthRequest(new JSONObject(), AUTH_ENDPOINT, HOST, apiKey, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    System.out.println(response.toString());
                    bearerToken = response.get("access_token").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {

            }
        });
    }
}
