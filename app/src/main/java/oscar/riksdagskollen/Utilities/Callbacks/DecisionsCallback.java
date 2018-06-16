package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Utilities.JSONModels.DecisionDocument;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public interface DecisionsCallback {

    void onDecisionsFetched(List<DecisionDocument> decisions);

    void onFail(VolleyError error);
}
