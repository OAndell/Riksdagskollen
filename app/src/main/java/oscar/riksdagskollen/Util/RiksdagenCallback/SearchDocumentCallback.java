package oscar.riksdagskollen.Util.RiksdagenCallback;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Util.JSONModel.SearchedDocument;


public interface SearchDocumentCallback {

    void onDocumentsFetched(List<SearchedDocument> documents);

    void onFail(VolleyError error);
}
