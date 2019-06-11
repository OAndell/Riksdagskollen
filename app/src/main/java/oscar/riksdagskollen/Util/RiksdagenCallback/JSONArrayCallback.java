package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface JSONArrayCallback {
    void onRequestSuccess(JSONArray response);

    void onRequestFail(VolleyError error);
}
