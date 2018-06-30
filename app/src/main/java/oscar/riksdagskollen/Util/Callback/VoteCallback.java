package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.Vote;


/**
 * Created by oscar on 2018-06-16.
 */

public interface VoteCallback {
    void onVotesFetched(List<Vote> votes);

    void onFail(VolleyError error);
}
