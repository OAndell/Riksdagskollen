package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

/**
 * Created by gustavaaro on 2018-03-28.
 */

public interface StringRequestCallback {

    public void onResponse(String response);
    public void onFail(VolleyError error);
}
