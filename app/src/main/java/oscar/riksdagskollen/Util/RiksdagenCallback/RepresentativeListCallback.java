package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.Representative;

/**
 * Created by oscar on 2018-09-23.
 */

public interface RepresentativeListCallback {
    void onPersonListFetched(List<Representative> representatives);

    void onFail(VolleyError error);
}
