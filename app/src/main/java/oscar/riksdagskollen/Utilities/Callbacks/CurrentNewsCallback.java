package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;

/**
 * Created by oscar on 2018-03-28.
 */

public interface CurrentNewsCallback {
    void onNewsFetched(List<CurrentNews> currentNews);

    void onFail(VolleyError error);
}
