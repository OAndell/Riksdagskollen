package oscar.riksdagskollen.Managers;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import junit.framework.Test;

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
import oscar.riksdagskollen.Utilities.Callbacks.RepresentativeCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.Callbacks.JSONRequestCallback;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagenAPIManager {

    RequestManager requestManager;
    Gson gson;
    String baseDocQueryURL = "/dokumentlista/?avd=dokument&del=dokument&fcs=1&sort=datum&sortorder=desc&utformat=json";


    public RiksdagenAPIManager(RikdagskollenApp app){
        requestManager = app.getRequestManager();
        gson = new Gson();
    }


    public void getDocumentsForParty(Party party,int page, final PartyDocumentCallback callback){
        String subURL = baseDocQueryURL + "&parti="+party.getID()+"&p="+page;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    System.out.println(jsonDocuments);
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

    public void getRepresentative(String iid, final RepresentativeCallback callback){
        String subURL = "/personlista/?iid="+iid+"&utformat=json";
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONObject jsonDocuments = response.getJSONObject("personlista").getJSONObject("person");
                    Representative representative = gson.fromJson(jsonDocuments.toString(),Representative.class);
                    callback.onPersonFetched(representative);
                } catch (JSONException e) {
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

    public void getDocumentBody(PartyDocument document, StringRequestCallback callback){
        requestManager.doStringGetRequest("http:" + document.getDokument_url_text(),callback);
    }

}
