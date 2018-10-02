package oscar.riksdagskollen.Util.RiksdagskollenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.RikdagsKollenModels.User;

/**
 * Created by gustavaaro on 2018-09-30.
 */

public interface AuthenticateCallback {

    void onAuthenticateComplete(String accesstoken, User user);

    void onAuthenticateFail(VolleyError error);
}
