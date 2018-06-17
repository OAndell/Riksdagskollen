package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Utilities.JSONModels.Vote;


/**
 * Created by oscar on 2018-06-16.
 */

public interface VoteCallback {
    void onVotesFetched(List<Vote> votes);

    void onFail(VolleyError error);
}
