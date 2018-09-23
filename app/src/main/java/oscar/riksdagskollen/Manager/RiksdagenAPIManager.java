package oscar.riksdagskollen.Manager;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Callback.CurrentNewsCallback;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.Callback.JSONRequestCallback;
import oscar.riksdagskollen.Util.Callback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.Callback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.Callback.ProtocolCallback;
import oscar.riksdagskollen.Util.Callback.RepresentativeCallback;
import oscar.riksdagskollen.Util.Callback.RepresentativeDocumentCallback;
import oscar.riksdagskollen.Util.Callback.RepresentativeListCallback;
import oscar.riksdagskollen.Util.Callback.StringRequestCallback;
import oscar.riksdagskollen.Util.Callback.VoteCallback;
import oscar.riksdagskollen.Util.JSONModel.CurrentNews;
import oscar.riksdagskollen.Util.JSONModel.CurrentNewsLink;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.JSONModel.Representative;
import oscar.riksdagskollen.Util.JSONModel.Vote;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagenAPIManager {

            private final RequestManager requestManager;
            private final Gson gson;


    public RiksdagenAPIManager(RiksdagskollenApp app) {
                requestManager = app.getRequestManager();
                gson = new Gson();
            }


    public void getDocumentsForParty(Party party, int page, final PartyDocumentCallback callback) {
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
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(CurrentNewsLink.class, new CurrentNewsLink.CurrentNewsLinkDeserializer())
                                    .create();
                            JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                            CurrentNews[] news = gson.fromJson(jsonDocuments.toString(), CurrentNews[].class);
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
            public Request getRepresentative(String iid, final RepresentativeCallback callback) {
                String subURL = "/personlista/?iid="+iid+"&utformat=json";
                return requestManager.doGetRequest(subURL, new JSONRequestCallback() {
                    @Override
                    public void onRequestSuccess(JSONObject response) {
                        try {
                            JSONObject jsonDocuments = response.getJSONObject("personlista").getJSONObject("person");
                            Representative representative = gson.fromJson(jsonDocuments.toString(), Representative.class);
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

    /**
     * Get representative from name and partyID
     *
     * @param fname    first name
     * @param ename    last name
     * @param partyID  ex V,S,M etc
     * @param callback
     */
    //Not sure if this even works with the API
    public void getRepresentative(String fname, String ename, String partyID, final String sourceId, final RepresentativeCallback callback) {
        String subURL = "/personlista/?iid=&fnamn=" + fname.trim() + "&ename=" + ename.trim() + "&parti=" + partyID + "&rdlstatus=samtliga&utformat=json";
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {

                                int hits = Integer.valueOf(response.getJSONObject("personlista").getString("@hits"));

                    // Multiple hits, need to search for correct representative
                    if (hits > 1) {
                        JSONArray returnedHits = response.getJSONObject("personlista").getJSONArray("person");
                        for (int i = 0; i < returnedHits.length(); i++) {
                            Representative representative = gson.fromJson(returnedHits.get(i).toString(), Representative.class);
                            if (representative.getSourceid().equals(sourceId)) {
                                callback.onPersonFetched(representative);
                                return;
                            }
                        }
                        callback.onFail(new VolleyError("Could not find representative"));
                        // Single hit
                    } else {
                        JSONObject jsonDocuments = response.getJSONObject("personlista").getJSONObject("person");
                        Representative representative = gson.fromJson(jsonDocuments.toString(), Representative.class);
                        callback.onPersonFetched(representative);
                    }
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
                            callback.onFail(new VolleyError("Could not parse response"));
                        }
                    }

                    @Override
                    public void onRequestFail(VolleyError error) {
                        callback.onFail(error);
                    }
                });
            }

            public void searchVotesForDecision(DecisionDocument decision, final VoteCallback callback){
                String subURL  = "/dokumentlista/?sok=" + decision.getRm() + ":" + decision.getBeteckning() + "&doktyp=votering&sort=datum&sortorder=desc&utformat=json";
                requestManager.doGetRequest(subURL, new JSONRequestCallback() {
                    @Override
                    public void onRequestSuccess(JSONObject response) {
                        try {
                            JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                            Vote[] protocols = gson.fromJson(jsonDocuments.toString(),Vote[].class);
                            callback.onVotesFetched(Arrays.asList(protocols));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFail(new VolleyError("Could not parse response"));
                        }
                    }

                    @Override
                    public void onRequestFail(VolleyError error) {
                        callback.onFail(error);
                    }
                });
            }

            public void searchForDecision(final DecisionsCallback callback, String search, int pageToLoad) {
                String subURL = "/dokumentlista/?sok=" + search + "&sort=rel&sortorder=desc&doktyp=bet&utformat=json" + "&p=" + pageToLoad;
                requestManager.doGetRequest(subURL, new JSONRequestCallback() {
                    @Override
                    public void onRequestSuccess(JSONObject response) {
                        try {
                            JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                            DecisionDocument[] decisionDocuments = gson.fromJson(jsonDocuments.toString(), DecisionDocument[].class);
                            callback.onDecisionsFetched(Arrays.asList(decisionDocuments));
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

    public void getAllCurrentRepresentatives(final RepresentativeListCallback callback) {
        String subUrl = "/personlista/?utformat=json";
        requestManager.doGetRequest(subUrl, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("personlista").getJSONArray("person");
                    Representative[] representatives = gson.fromJson(jsonDocuments.toString(), Representative[].class);
                    callback.onPersonListFetched(Arrays.asList(representatives));
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

    public void getRepresentativesInParty(String party, final RepresentativeListCallback callback) {
        String subUrl = "/personlista/?parti=" + party + "&utformat=json";
        requestManager.doGetRequest(subUrl, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("personlista").getJSONArray("person");
                    Representative[] representatives = gson.fromJson(jsonDocuments.toString(), Representative[].class);
                    callback.onPersonListFetched(Arrays.asList(representatives));
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


    public void getMotionByID(String id, final PartyDocumentCallback callback){
                String subURL = "/dokumentlista/?sok=\"" + id + "\"&doktyp=mot&sort=datum&sortorder=desc&utformat=json";
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

            public void getDocumentsForRepresentative(String intressentId, int page, final RepresentativeDocumentCallback callback) {

                String subURL = "/dokumentlista/?avd=dokument&del=dokument&fcs=1&sort=datum&sortorder=desc&utformat=json"
                        + "&iid=" + intressentId
                        + "&p=" + page;

                requestManager.doGetRequest(subURL, new JSONRequestCallback() {
                    @Override
                    public void onRequestSuccess(JSONObject response) {
                        try {
                            JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                            PartyDocument[] documents = gson.fromJson(jsonDocuments.toString(), PartyDocument[].class);
                            String hits = response.getJSONObject("dokumentlista").getString("@traffar");
                            callback.onDocumentsFetched(Arrays.asList(documents), hits);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (e.getMessage().equals("No value for dokument"))
                                callback.onFail(new VolleyError("no_docs"));
                            else callback.onFail(new VolleyError("Failed to parse JSON"));
                        }
                    }

                    @Override
                    public void onRequestFail(VolleyError error) {
                        callback.onFail(error);
                    }
                });

            }

    public void searchForReply(PartyDocument document, final PartyDocumentCallback callback){
        String subURL = "/dokumentlista/?sok="+document.getRm()+":"+ document.getBeteckning()+"&doktyp=frs&sort=datum&sortorder=desc&utformat=json";
        requestManager.doGetRequest(subURL, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    PartyDocument[] documents = gson.fromJson(jsonDocuments.toString(), PartyDocument[].class);
                    callback.onDocumentsFetched(Arrays.asList(documents));
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


    public void searchForQuestion(PartyDocument document, final PartyDocumentCallback callback) {
        String subURL = "/dokumentlista/?sok=" + document.getRm() + ":" + document.getBeteckning() + "&doktyp=fr&sort=datum&sortorder=desc&utformat=json";
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


            public AsyncTask getPartyLeaders(String partyName, final PartyLeadersCallback callback) {
                //need to remove swedish chars
                partyName = partyName.replace("ö","o");
                partyName = partyName.replace("ä","a");
                String partyInfoUrl = "http://riksdagen.se/sv/ledamoter-partier/"+partyName;
                return requestManager.downloadHtmlPage(partyInfoUrl, new StringRequestCallback() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Representative> representatives = new ArrayList<>();
                        Document doc = Jsoup.parse(response);
                        Elements leadersList = doc.getElementsByClass("fellow-item");
                        for (int i = 0; i < leadersList.size(); i++) {
                            String name = leadersList.get(i).getElementsByClass("fellow-name").text();
                            String position = leadersList.get(i).getElementsByClass("fellow-position").text();
                            String imageURL = leadersList.get(i).select("img").first().absUrl("src");
                            if(position.equals("")){
                                break;
                            }
                            String[] nameArr = name.split(" ");
                            String firstName = nameArr[0];
                            String lastName = "";
                            for (int j = 1; j < nameArr.length; j++) {
                                lastName += nameArr[j] + " ";
                            }
                            representatives.add(new Representative(firstName, lastName,position,imageURL));
                        }
                        callback.onPersonFetched(representatives);
                    }

                    @Override
                    public void onFail(VolleyError error) {

                    }
                });


            }

}
