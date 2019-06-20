package oscar.riksdagskollen.Manager;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import oscar.riksdagskollen.BuildConfig;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.JSONModel.Twitter.Tweet;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONArrayCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.TwitterCallback;

interface AuthenticateCallback {
    void onAuth();
}

public class TwitterAPIManager {

    private static final String HOST = "https://api.twitter.com/";
    private static final String AUTH_ENDPOINT = "oauth2/token?grant_type=client_credentials";
    private static final String TIMELINE_ENDPOINT = "1.1/statuses/user_timeline.json";
    private static final String LIST_ENDPOINT = "/1.1/lists/statuses.json";

    private RequestManager requestManager;
    private Gson gson;
    private String bearerToken;
    private String apiKey = BuildConfig.TwitterApiKey;
    private boolean hasAuth = false;


    public TwitterAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new Gson();
    }

    public TwitterAPIManager getInstance() {
        return RiksdagskollenApp.getInstance().getTwitterAPIManager();
    }


    public void getTweets(final String screenName, final TwitterCallback callback, boolean includeRT) {
        String subURL = TIMELINE_ENDPOINT + "?screen_name=" + screenName
                + "&tweet_mode=extended"; //TODO trim user might be a good idea here since we already know it.
        if (includeRT) {
            subURL = subURL + "&include_rts=true";
        }
        doGetTweetRequest(subURL, callback);
    }


    public void getTweetsSinceID(final String screenName, final TwitterCallback callback, boolean includeRT, long id) {
        String subURL = TIMELINE_ENDPOINT + "?screen_name=" + screenName
                + "&tweet_mode=extended&&max_id=" + id;
        if (includeRT) {
            subURL = subURL + "&include_rts=true";
        }
        doGetTweetRequest(subURL, callback);
    }

    public void getTweetList(String ownerScreenname, String slug, TwitterCallback callback, boolean includeRT) {
        String subURL = LIST_ENDPOINT + "?owner_screen_name=" + ownerScreenname + "&slug=" + slug
                + "&tweet_mode=extended";
        if (includeRT) {
            subURL = subURL + "&include_rts=true";
        }
        doGetTweetRequest(subURL, callback);
    }

    public void getTweetListSinceID(String ownerScreenname, String slug, TwitterCallback callback, boolean includeRT, long id) {
        String subURL = LIST_ENDPOINT + "?owner_screen_name=riksdagskollen&slug=riksdagskollen"
                + "&tweet_mode=extended&max_id=" + id;
        if (includeRT) {
            subURL = subURL + "&include_rts=true";
        }
        doGetTweetRequest(subURL, callback);
    }


    private void doGetTweetRequest(final String subURL, final TwitterCallback callback) {
        if (hasAuth) {
            requestManager.doTwitterGetRequest(subURL, HOST, bearerToken, new JSONArrayCallback() {
                @Override
                public void onRequestSuccess(JSONArray response) {
                    Tweet[] tweets = gson.fromJson(response.toString(), Tweet[].class);
                    callback.onTweetsFetched(Arrays.asList(tweets));
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    System.out.println("Error: " + error.toString());
                }
            });
        } else {
            System.out.println("Not Authenticated");
            Authenticate(new AuthenticateCallback() {
                @Override
                public void onAuth() {
                    doGetTweetRequest(subURL, callback);
                }
            });
        }
    }

    private void Authenticate(final AuthenticateCallback callback) {
        requestManager.doTwitterAuthRequest(new JSONObject(), AUTH_ENDPOINT, HOST, apiKey, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    bearerToken = response.get("access_token").toString();
                    hasAuth = true;
                    System.out.println("Authenticated");
                    System.out.println("Token " + bearerToken);
                    callback.onAuth();
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
