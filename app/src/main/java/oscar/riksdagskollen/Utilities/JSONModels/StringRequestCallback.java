package oscar.riksdagskollen.Utilities.JSONModels;

import com.android.volley.VolleyError;

/**
 * Created by gustavaaro on 2018-03-28.
 */

public interface StringRequestCallback {

    public void onResponse(String response);
    public void onFail(VolleyError error);
}
