package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;



/**
 * Created by oscar on 2018-03-27.
 */

public interface RepresentativeCallback {

    void onPersonFetched();

    void onFail(VolleyError error);
}
