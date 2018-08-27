package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import oscar.riksdagskollen.Util.JSONModel.Representative;

/**
 * Created by oscar on 2018-08-27.
 */

public interface PartyLeadersCallback {

    void onPersonFetched(ArrayList<Representative> leaders);

    void onFail(VolleyError error);

}
