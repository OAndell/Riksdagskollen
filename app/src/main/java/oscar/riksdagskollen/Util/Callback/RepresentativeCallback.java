package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;


/**
 * Created by oscar on 2018-03-27.
 */

public interface RepresentativeCallback {

    void onPersonFetched(Representative representative);

    void onFail(VolleyError error);
}
