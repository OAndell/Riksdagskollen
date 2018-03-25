package oscar.riksdagskollen.Managers;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.Callbacks.JSONRequestCallback;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagenAPIManager {

    RequestManager requestManager;
    Gson gson;
    String baseQueryURL = "?avd=dokument&del=dokument&fcs=1&sort=datum&sortorder=desc&utformat=json";

    public RiksdagenAPIManager(RikdagskollenApp app){
        requestManager = app.getRequestManager();
        gson = new Gson();
    }


    public void getDocumentsForParty(Party party, final PartyDocumentCallback callback){
        String subURL = baseQueryURL + "&parti="+party.getID();
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    PartyDocument[] documents = gson.fromJson(jsonDocuments.toString(),PartyDocument[].class);
                    callback.onDocumentsFetched(Arrays.asList(documents));
                }catch (JSONException e){
                    e.printStackTrace();
                    callback.onFail(new VolleyError("Failed to parse JSON"));
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }


}
