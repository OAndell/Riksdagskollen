package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.CurrentNewsModels.CurrentNews;

/**
 * Created by oscar on 2018-03-28.
 */

public interface CurrentNewsCallback {
    void onNewsFetched(List<CurrentNews> currentNews);

    void onFail(VolleyError error);
}
