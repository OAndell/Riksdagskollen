package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Utilities.JSONModels.Representative;


/**
 * Created by oscar on 2018-03-27.
 */

public interface RepresentativeCallback {

    void onPersonFetched(Representative representative);

    void onFail(VolleyError error);
}
