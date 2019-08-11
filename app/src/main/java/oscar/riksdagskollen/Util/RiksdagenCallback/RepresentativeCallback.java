package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.RepresentativeList.data.Representative;


/**
 * Created by oscar on 2018-03-27.
 */

public interface RepresentativeCallback {

    void onPersonFetched(Representative representative);

    void onFail(VolleyError error);
}
