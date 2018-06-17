package oscar.riksdagskollen.Managers;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.Callbacks.DecisionsCallback;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.Callbacks.ProtocolCallback;
import oscar.riksdagskollen.Utilities.Callbacks.RepresentativeCallback;
import oscar.riksdagskollen.Utilities.Callbacks.VoteCallback;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.DecisionDocument;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.Callbacks.JSONRequestCallback;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.JSONModels.Protocol;
import oscar.riksdagskollen.Utilities.JSONModels.Representative;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Vote;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagenAPIManager {

    private RequestManager requestManager;
    private Gson gson;


    public RiksdagenAPIManager(RikdagskollenApp app){
        requestManager = app.getRequestManager();
        gson = new Gson();
    }


    public void getDocumentsForParty(Party party,int page, final PartyDocumentCallback callback){
        String subURL = "/dokumentlista/?avd=dokument&del=dokument&fcs=1&sort=datum&sortorder=desc&utformat=json"
                + "&parti=" + party.getID()
                + "&p=" + page;
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

    /**
     * Get the current news (Aktuellt)
     * @param callback callback which a List of currentNews is returned to
     */
    public void getCurrentNews(final CurrentNewsCallback callback, int page){
        String subURL = "/dokumentlista/?avd=aktuellt&sort=datum&sortorder=desc&lang=sv&cmskategori=startsida&utformat=json"
                + "&p=" + page;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    CurrentNews[] news = gson.fromJson(jsonDocuments.toString(),CurrentNews[].class);
                    callback.onNewsFetched(Arrays.asList(news));
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

    /**
     * Get decisions
     * @param callback callback which a List of the latest decisions is returned to
     */
    public void getDecisions(final DecisionsCallback callback, int page){
        String subURL = "/dokumentlista/?doktyp=bet&sort=datum&sortorder=desc&dokstat=beslutade&utformat=json" + "&p=" + page;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    DecisionDocument[] decisionDocuments = gson.fromJson(jsonDocuments.toString(),DecisionDocument[].class);
                    callback.onDecisionsFetched(Arrays.asList(decisionDocuments));
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

    /**
     * Get decisions
     * @param callback callback which a List of one specific decision is returned to
     */
    public void getDecisionWithId(final DecisionsCallback callback, String id){
        String subURL = "/dokumentlista/?doktyp=bet&utformat=json&dok_id="+id;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    DecisionDocument[] decisionDocuments = gson.fromJson(jsonDocuments.toString(),DecisionDocument[].class);
                    callback.onDecisionsFetched(Arrays.asList(decisionDocuments));
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

    /**
     * Get representative(ledamot) information by intressent_id.
     * @param iid intressent_id for the representative
     * @param callback callback function which the a Representative JSON model is returned.
     */
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
        if(document == null){
            // Very long motion, used for testing by sending null as an argument
            requestManager.downloadHtmlPage("http://data.riksdagen.se/dokument/H5023752.html",callback);
        }else if(document.isMotion()){
            requestManager.downloadHtmlPage("http:" + document.getDokument_url_html(), callback);
        } else {
            requestManager.doStringGetRequest("http:" + document.getDokument_url_html(),callback);
        }
    }


    public void getNewsHTML(String url, StringRequestCallback callback) {
        String fullURL = "http://riksdagen.se" + url;
        requestManager.downloadHtmlPage(fullURL,callback);
    }


    public void getProtocols(final ProtocolCallback callback,  int page){
        String subURL = "/dokumentlista/?sok=&doktyp=prot&sort=datum&sortorder=desc&utformat=json" + "&p=" + page;;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Protocol[] protocols = gson.fromJson(jsonDocuments.toString(),Protocol[].class);
                    callback.onProtocolsFetched(Arrays.asList(protocols));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }

    public void getVotes(final VoteCallback callback, int page){
        String subURL = "/dokumentlista/?sok=&doktyp=votering&sort=datum&sortorder=desc&utformat=json" + "&p=" + page;;
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Vote[] protocols = gson.fromJson(jsonDocuments.toString(),Vote[].class);
                    callback.onVotesFetched(Arrays.asList(protocols));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }



}
