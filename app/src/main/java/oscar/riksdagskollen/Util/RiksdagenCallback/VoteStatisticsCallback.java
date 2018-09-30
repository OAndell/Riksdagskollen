package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.RepresentativeVoteStatistics;

/**
 * Created by oscar on 2018-09-29.
 */

public interface VoteStatisticsCallback {
    void onStatisticsFetched(RepresentativeVoteStatistics stats);

    void onFail(VolleyError error);
}
