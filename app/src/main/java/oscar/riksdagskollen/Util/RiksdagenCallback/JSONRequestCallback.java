package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public interface JSONRequestCallback {

    void onRequestSuccess(JSONObject response);

    void onRequestFail(VolleyError error);
}
