package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by oscar on 2018-08-27.
 */

public interface PartyLeadersCallback {

    void onPersonFetched(ArrayList<Representative> leaders);

    void onFail(VolleyError error);

}
