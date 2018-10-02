package oscar.riksdagskollen.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

import oscar.riksdagskollen.BuildConfig;
import oscar.riksdagskollen.Util.JSONModel.RikdagsKollenModels.User;
import oscar.riksdagskollen.Util.RiksdagskollenCallback.AuthenticateCallback;
import oscar.riksdagskollen.Util.RiksdagskollenCallback.UserCallback;

/**
 * Created by gustavaaro on 2018-09-30.
 */

public class AnonymousAccountManager {
    private static final String TAG = "AccountManager";

    public final static String TOKEN = "token";
    public final static String PARTY = "party";
    public final static String USER_ID = "user_id";

    Context context;
    private SharedPreferences preferences;
    private User user = null;

    public AnonymousAccountManager(Context appContext) {
        this.context = appContext;
        preferences = appContext.getSharedPreferences("user", 0);
        initialSetup();
    }

    private boolean isAuthenticated() {
        return getAccessToken() != null;
    }

    private void initialSetup() {

        // No access token received from server, initiate identification process
        if (!isAuthenticated()) {
            getSafetyNetAttestationJWS(new AttestationListener() {
                @Override
                public void onResponse(String jws) {
                    RiksdagskollenAPIManager.getInstance().authenticate(jws, new AuthenticateCallback() {
                        @Override
                        public void onAuthenticateComplete(String accessToken, User user) {
                            setAccessToken(accessToken);
                            setUser(user);
                        }

                        @Override
                        public void onAuthenticateFail(VolleyError error) {
                            Log.d(TAG, "onAuthenticateFail: " + new String(error.networkResponse.data));
                        }
                    });
                }
            });
        } else if (getUser() == null) {
            RiksdagskollenAPIManager.getInstance().getUser(getAccessToken(), new UserCallback() {
                @Override
                public void onUserFetched(User user) {
                    setUser(user);
                }

                @Override
                public void onError(VolleyError error) {
                    Log.d(TAG, "onError: " + error.networkResponse);
                    setUser(null);
                }
            });
        }
    }

    private void setAccessToken(String accessToken) {
        preferences.edit().putString(TOKEN, accessToken).apply();
    }

    public User getUser() {
        if (user != null) return user;

        String id = preferences.getString(USER_ID, null);
        String party = preferences.getString(PARTY, null);
        if (id != null) {
            this.user = new User(id, party);
        }
        Log.d(TAG, "getUser: user is " + user);

        return this.user;
    }

    private void setUser(User user) {
        this.user = user;
        Log.d(TAG, "setUser: user set to " + this.user);
        if (user != null) {
            preferences.edit().putString(USER_ID, user.getId()).apply();
            preferences.edit().putString(PARTY, user.getParty()).apply();
        }
    }


    public String getAccessToken() {
        return preferences.getString(TOKEN, null);
    }


    private void getSafetyNetAttestationJWS(final AttestationListener listener) {
        Log.d(TAG, "getSafetyNetAttestationJWS: ");
        final String api = BuildConfig.SafetyApiKey;
        byte[] nonce = new byte[16];
        new Random().nextBytes(nonce);
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context.getApplicationContext()) == ConnectionResult.SUCCESS) {
            SafetyNet.getClient(context).attest(nonce, api).addOnSuccessListener(new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                @Override
                public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
                    listener.onResponse(attestationResponse.getJwsResult());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        } else {
            Log.d(TAG, "getSafetyNetAttestationJWS: GPS not available");
        }

    }

    private interface AttestationListener {
        void onResponse(String jws);
    }
}
