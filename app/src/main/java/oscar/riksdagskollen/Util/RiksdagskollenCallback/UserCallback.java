package oscar.riksdagskollen.Util.RiksdagskollenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.RikdagsKollenModels.User;

/**
 * Created by gustavaaro on 2018-10-01.
 */

public interface UserCallback {

    void onUserFetched(User user);

    void onError(VolleyError error);
}
