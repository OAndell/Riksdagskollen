package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.DeviceUuidFactory;
import oscar.riksdagskollen.Util.JSONModel.RikdagsKollenModels.User;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagskollenCallback.AuthenticateCallback;
import oscar.riksdagskollen.Util.RiksdagskollenCallback.UserCallback;

/**
 * Created by gustavaaro on 2018-09-30.
 */

public class RiksdagskollenAPIManager {


    private final RequestManager requestManager;
    private final Gson gson;
    private static final String HOST = "https://riksdagskollen.herokuapp.com";
    private Context context;


    public RiksdagskollenAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new Gson();
        this.context = app;
    }

    static RiksdagskollenAPIManager getInstance() {
        return RiksdagskollenApp.getInstance().getRiksdagskollenAPIManager();
    }

    private void doApiGetRequest(String subUrl, JSONRequestCallback callback) {
        requestManager.doGetRequest(subUrl, HOST, callback);
    }

    private void doAuthenticatedApiGetRequest(String subUrl, String accessToken, JSONRequestCallback callback) {
        requestManager.doAuthenticatedGetRequest(subUrl, HOST, accessToken, callback);
    }

    private void doApiPostRequest(String subUrl, JSONObject body, JSONRequestCallback callback) {
        System.out.println("URL: " + HOST + subUrl);
        System.out.println(body.toString());
        requestManager.doPostRequest(body, subUrl, HOST, callback);
    }

    public void getUser(String accessToken, final UserCallback callback) {
        doAuthenticatedApiGetRequest("/api/user/me", accessToken, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                User user = gson.fromJson(response.toString(), User.class);
                Log.d("AnonymousAccountManager", "onAPIUSERRequestSuccess: " + response.toString());
                callback.onUserFetched(user);
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    public void authenticate(String jws, final AuthenticateCallback authenticateCallback) {
        JSONObject body = new JSONObject();
        String id = DeviceUuidFactory.getDeviceUuid(context);
        try {
            body.put("jws", jws);
            body.put("android_id", id);
        } catch (JSONException e) {
            authenticateCallback.onAuthenticateFail(new VolleyError("Could not complete request"));
            return;
        }
        doApiPostRequest("/api/user", body, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    String token = response.getString("token");
                    String userObject = response.getJSONObject("user").toString();
                    Log.d("AnonymousAccountManager", "onAPIRequestSuccess: " + userObject);
                    User user = gson.fromJson(userObject, User.class);
                    authenticateCallback.onAuthenticateComplete(token, user);
                } catch (JSONException e) {
                    authenticateCallback.onAuthenticateFail(new VolleyError("Could not parse response"));
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {
                authenticateCallback.onAuthenticateFail(error);
            }
        });


    }


}
