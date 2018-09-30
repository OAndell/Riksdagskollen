package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public interface DecisionsCallback {

    void onDecisionsFetched(List<DecisionDocument> decisions);

    void onFail(VolleyError error);
}
