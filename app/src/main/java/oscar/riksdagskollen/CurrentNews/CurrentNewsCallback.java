package oscar.riksdagskollen.CurrentNews;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.CurrentNews.CurrentNewsJSONModels.CurrentNews;

/**
 * Created by oscar on 2018-03-28.
 */

public interface CurrentNewsCallback {
    void onNewsFetched(List<CurrentNews> currentNews);

    void onFail(VolleyError error);
}
