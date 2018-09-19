package oscar.riksdagskollen.Util.Callback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.PartyDocument;

/**
 * Created by gustavaaro on 2018-09-19.
 */

public interface RepresentativeDocumentCallback {

    void onDocumentsFetched(List<PartyDocument> documents, String numberOfHits);

    void onFail(VolleyError error);
}
