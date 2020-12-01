package oscar.riksdagskollen.Manager;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import oscar.riksdagskollen.DebateList.Data.Debate;
import oscar.riksdagskollen.DebateView.Data.Speech;
import oscar.riksdagskollen.News.CurrentNewsCallback;
import oscar.riksdagskollen.News.Data.CurrentNews;
import oscar.riksdagskollen.News.Data.CurrentNewsLink;
import oscar.riksdagskollen.RepresentativeList.data.Representative;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.CacheRequest;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.JSONModel.Party;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Protocol;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.RepresentativeInfo;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.RepresentativeInfoList;
import oscar.riksdagskollen.Util.JSONModel.RepresentativeModels.RepresentativeVoteStatistics;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.DebateAudioSourceCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.DecisionsCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.JSONRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyLeadersCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.ProtocolCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.RepresentativeListCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.SpeechCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.VoteStatisticsCallback;


/**
 * Created by gustavaaro on 2018-03-25.
 */

public class RiksdagenAPIManager {


    public static String SEARCH_OPTION_REL = "rel";
    public static String SEARCH_OPTION_DATE = "datum";

    private final RequestManager requestManager;
    private final Gson gson;
    private static final String HOST = "https://data.riksdagen.se";


    public RiksdagenAPIManager(RiksdagskollenApp app) {
        requestManager = app.getRequestManager();
        gson = new GsonBuilder()
                .registerTypeAdapter(Debate.class, new Debate.DebateDezerializer())
                .registerTypeAdapter(RepresentativeInfo.class, new RepresentativeInfo.RepresentativeInfoDezerializer())
                .registerTypeAdapter(RepresentativeInfoList.class, new RepresentativeInfoList.RepresentativeInfoListDeserializer())
                .registerTypeAdapter(CurrentNewsLink.class, new CurrentNewsLink.CurrentNewsLinkDeserializer())
                .create();
    }

    public static RiksdagenAPIManager getInstance() {
        return RiksdagskollenApp.getInstance().getRiksdagenAPIManager();
    }

    private Request doApiGetRequest(String subURL, JSONRequestCallback callback) {
        return requestManager.doGetRequest(subURL, HOST, callback);
    }

    private Request doCachedApiGetRequest(String subURL, CacheRequest.CachingPolicy cachingPolicy, JSONRequestCallback callback) {
        return requestManager.doCachedGetRequest(subURL, HOST, cachingPolicy, callback);
    }

    private void doCachedApiGetStringRequest(String url, CacheRequest.CachingPolicy cachingPolicy, StringRequestCallback callback) {
        requestManager.doCachedStringGetRequest(url, cachingPolicy, callback);
    }

    private void doApiGetStringRequest(String url, StringRequestCallback callback) {
        requestManager.doStringGetRequest(url, callback);
    }

    public void getDocumentsForParty(Party party, int page, final PartyDocumentCallback callback) {
        getDocumentsForParty(party.getID(), page, callback);
    }

    public void getDocumentsForParty(String partyid, int page, final PartyDocumentCallback callback) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("avd", "dokument")
                .appendQueryParameter("del", "dokument")
                .appendQueryParameter("fcs", "1")
                .appendQueryParameter("parti", partyid)
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(final JSONObject response) {
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

    public void getDebates(int page, final PartyDocumentCallback callback) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "ip,bet,kam-ad")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("webbtv", "1")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(final JSONObject response) {

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

    /**
     * Get the current news (Aktuellt)
     *
     * @param callback callback which a List of currentNews is returned to
     */
    public void getCurrentNews(final CurrentNewsCallback callback, int page) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("avd", "aktuellt")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("lang", "sv")
                .appendQueryParameter("cmskategori", "startsida")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");

                    CurrentNews[] news = gson.fromJson(jsonDocuments.toString(), CurrentNews[].class);
                    callback.onNewsFetched(Arrays.asList(news));
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
     * Get decisions
     *
     * @param callback callback which a List of the latest decisions is returned to
     */
    public void getDecisions(final DecisionsCallback callback, int page) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "bet")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("dokstat", "beslutade")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(final JSONObject response) {
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

    /**
     * Get decisions
     *
     * @param callback callback which a List of one specific decision is returned to
     */
    public void getDecisionWithId(final DecisionsCallback callback, String id) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "bet")
                .appendQueryParameter("dok_id", id)
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
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

    /**
     * Get representative(ledamot) information by intressent_id.
     *
     * @param iid      intressent_id for the representative
     * @param callback callback function which the a Representative JSON model is returned.
     */
    public Request getRepresentative(String iid, final RepresentativeCallback callback) {

        // Check if downloaded already
        Representative downloadedRep = RiksdagskollenApp.getInstance().getRepresentativeManager().getRepresentative(iid, null);
        if (downloadedRep != null) {
            callback.onPersonFetched(downloadedRep);
            // ugly fix to return a request if representative was already downloaded
            return new JsonArrayRequest(null, null, null);
        } else {

            String subURL = Uri.parse("/personlista/")
                    .buildUpon()
                    .appendQueryParameter("iid", iid)
                    .appendQueryParameter("utformat", "json")
                    .build().toString();

            return doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
                @Override
                public void onRequestSuccess(final JSONObject response) {
                    try {
                        JSONObject jsonDocuments = response.getJSONObject("personlista").getJSONObject("person");
                        Representative representative = gson.fromJson(jsonDocuments.toString(), Representative.class);
                        // Save to avoid re-download
                        RiksdagskollenApp.getInstance().getRepresentativeManager().addRepresentative(representative);
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
        String subURL = Uri.parse("/personlista/")
                .buildUpon()
                .appendQueryParameter("fnamn", fname.trim())
                .appendQueryParameter("ename", ename.trim())
                .appendQueryParameter("parti", partyID)
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("rdlstatus", "samtliga")
                .build().toString();

        Representative representative = RiksdagskollenApp.getInstance().getRepresentativeManager().findRepresentative(partyID, sourceId);

        if (representative != null) {
            callback.onPersonFetched(representative);
            return;
        }

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(final JSONObject response) {
                try {
                    int hits = Integer.valueOf(response.getJSONObject("personlista").getString("@hits"));
                    // Multiple hits, need to search for correct representative
                    if (hits > 1) {
                        JSONArray returnedHits = response.getJSONObject("personlista").getJSONArray("person");
                        for (int i = 0; i < returnedHits.length(); i++) {
                            Representative representative = gson.fromJson(returnedHits.get(i).toString(), Representative.class);
                            if (representative.getSourceid().equals(sourceId)) {
                                RiksdagskollenApp.getInstance().getRepresentativeManager().addRepresentative(representative);
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

    public void getDocumentBody(PartyDocument document, StringRequestCallback callback) {

        // Documents are very seldom (if ever) subject to change, use aggressive caching
        if (document.isMotion()) {
            requestManager.doCachedStringGetRequest("http:" + document.getDokument_url_html(), CacheRequest.CachingPolicy.LONG_TIME_CACHE, callback);
        } else {
            doCachedApiGetStringRequest("http:" + document.getDokument_url_html(), CacheRequest.CachingPolicy.LONG_TIME_CACHE, callback);
        }
    }

    public void getProtocols(final ProtocolCallback callback, int page) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "prot")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Protocol[] protocols = gson.fromJson(jsonDocuments.toString(), Protocol[].class);
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

    public void getProtocolForDate(String date, String rm, final ProtocolCallback callback) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "prot")
                .appendQueryParameter("rm", rm)
                .appendQueryParameter("from", date)
                .appendQueryParameter("tom", date)
                .appendQueryParameter("sort", SEARCH_OPTION_REL)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {

                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Protocol[] protocols = gson.fromJson(jsonDocuments.toString(), Protocol[].class);
                    callback.onProtocolsFetched(Arrays.asList(protocols));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(new VolleyError(e.getMessage()));
                }

            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }


    public void getSpeech(String protId, final String speechNo, final SpeechCallback callback) {

        String subUrl = Uri.parse("/anforande/").buildUpon()
                .appendEncodedPath(protId + "-" + speechNo)
                .appendEncodedPath("json")
                .toString();

        doCachedApiGetRequest(subUrl, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONObject speechJSON = response.getJSONObject("anforande");
                    Speech speech = gson.fromJson(speechJSON.toString(), Speech.class);
                    callback.onSpeechFetched(speech, speechNo);
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

    public void getVotes(final VoteCallback callback, int page) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("doktyp", "votering")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Vote[] protocols = gson.fromJson(jsonDocuments.toString(), Vote[].class);
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

    public void searchVotesForDecision(DecisionDocument decision, final VoteCallback callback) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", decision.getRm() + ":" + decision.getBeteckning())
                .appendQueryParameter("doktyp", "votering")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONArray jsonDocuments = response.getJSONObject("dokumentlista").getJSONArray("dokument");
                    Vote[] protocols = gson.fromJson(jsonDocuments.toString(), Vote[].class);
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

    /**
     * Search query for all document types with search string
     */
    public void searchForDocument(String search, String sortOption, int pageToLoad, final PartyDocumentCallback callback) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", search)
                .appendQueryParameter("sort", sortOption)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(pageToLoad))
                .build().toString();

        doApiGetRequest(subURL, new JSONRequestCallback() {
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

    public void searchForDecision(final DecisionsCallback callback, String search, int pageToLoad) {
        String subUrl = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", search)
                .appendQueryParameter("doktyp", "bet")
                .appendQueryParameter("sort", "rel")
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(pageToLoad))
                .build().toString();

        doApiGetRequest(subUrl, new JSONRequestCallback() {
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
        String subUrl = Uri.parse("/personlista/")
                .buildUpon()
                .appendQueryParameter("utformat", "json")
                .build().toString();

        // Current representatives
        doCachedApiGetRequest(subUrl, CacheRequest.CachingPolicy.LONG_TIME_CACHE, new JSONRequestCallback() {
            @Override
            public void onRequestSuccess(final JSONObject response) {
                // Handle JSON-parsing on async thread to avoid lockup
                final Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Representative[] representatives = (Representative[]) msg.getData().getParcelableArray("representatives");
                        if (representatives != null)
                            callback.onPersonListFetched(Arrays.asList(representatives));
                        else callback.onFail(new VolleyError("Could not get representatives"));
                        super.handleMessage(msg);
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonDocuments = response.getJSONObject("personlista").getJSONArray("person");
                            Representative[] representatives = gson.fromJson(jsonDocuments.toString(), Representative[].class);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArray("representatives", representatives);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }

    public void getCurrentRepresentativesInParty(String party, final RepresentativeListCallback callback) {

        ArrayList<Representative> repsForParty = RiksdagskollenApp.getInstance().getRepresentativeManager().getCurrentRepresentativesForParty(party);
        if (repsForParty != null && !repsForParty.isEmpty()) {
            callback.onPersonListFetched(repsForParty);
        } else {
            String subUrl = Uri.parse("/personlista/")
                    .buildUpon()
                    .appendQueryParameter("parti", party)
                    .appendQueryParameter("utformat", "json")
                    .build().toString();

            doCachedApiGetRequest(subUrl, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
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

    }


    public void getMotionByID(String id, final PartyDocumentCallback callback) {
        String subUrl = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", id)
                .appendQueryParameter("doktyp", "mot,prop")
                .appendQueryParameter("sort", "datum")
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subUrl, CacheRequest.CachingPolicy.LONG_TIME_CACHE, new JSONRequestCallback() {
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

    public void getDocumentsForRepresentative(String intressentId, int page, final RepresentativeDocumentCallback callback) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("avd", "dokument")
                .appendQueryParameter("iid", intressentId)
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .appendQueryParameter("p", Integer.toString(page))
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
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

    public void searchForReply(PartyDocument document, final PartyDocumentCallback callback) {
        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", document.getRm() + ":" + document.getBeteckning())
                .appendQueryParameter("doktyp", "frs")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
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

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", document.getRm() + ":" + document.getBeteckning())
                .appendQueryParameter("doktyp", "fr")
                .appendQueryParameter("sort", SEARCH_OPTION_DATE)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.SHORT_TIME_CACHE, new JSONRequestCallback() {
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

    public void getDocument(String docId, final PartyDocumentCallback callback) {

        String subURL = Uri.parse("/dokumentlista/")
                .buildUpon()
                .appendQueryParameter("sok", docId)
                .appendQueryParameter("sort", SEARCH_OPTION_REL)
                .appendQueryParameter("sortorder", "desc")
                .appendQueryParameter("utformat", "json")
                .build().toString();

        doCachedApiGetRequest(subURL, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new JSONRequestCallback() {
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


    public void getPartyLeaders(String partyName, final PartyLeadersCallback callback) {

        // Need to remove swedish chars
        partyName = partyName.replace("ö", "o");
        partyName = partyName.replace("ä", "a");

        String partyInfoUrl = Uri.parse("https://riksdagen.se/sv/ledamoter-partier/")
                .buildUpon()
                .appendPath(partyName)
                .build().toString();

        requestManager.getDownloadString(partyInfoUrl, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                ArrayList<Representative> representatives = new ArrayList<>();
                Document doc = Jsoup.parse(response);
                Elements leadersList = doc.getElementsByClass("fellow-item");
                for (int i = 0; i < leadersList.size(); i++) {
                    String name = leadersList.get(i).getElementsByClass("fellow-name").text();
                    String position = leadersList.get(i).getElementsByClass("fellow-position").text();
                    String imageURL = leadersList.get(i).select("img").first().absUrl("src");
                    if (position.equals("")) {
                        break;
                    }
                    String[] nameArr = name.split(" ");
                    String firstName = nameArr[0];
                    String lastName = "";
                    for (int j = 1; j < nameArr.length; j++) {
                        lastName += nameArr[j] + " ";
                    }
                    representatives.add(new Representative(firstName, lastName, position, imageURL));
                }
                callback.onPersonFetched(representatives);
            }

            @Override
            public void onFail(VolleyError error) {
                callback.onFail(error);
            }
        });
    }


    public void getDebateAudioSource(PartyDocument debateDocument, DebateAudioSourceCallback callback) {

        String url = Uri.parse("https://data.riksdagen.se/dokumentstatus/")
                .buildUpon()
                .appendEncodedPath(debateDocument.getId() + ".webbtvxml")
                .build().toString();

        doCachedApiGetStringRequest(url, CacheRequest.CachingPolicy.LONG_TIME_CACHE, new StringRequestCallback() {
            @Override
            public void onResponse(String xml) {
                String url = "";
                try {
                    Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
                    Elements els = doc.select("dokumentstatus > webbmedia > media > bandbredd > kvalitet > url");
                    url = els.first().text();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onAudioSourceFail(new VolleyError("Failed to parse XML"));
                }
                if (url.isEmpty())
                    callback.onAudioSourceFail(new VolleyError("Failed to get audio source url"));
                else callback.onDebateAudioSource(url);
            }

            @Override
            public void onFail(VolleyError error) {
                callback.onAudioSourceFail(error);
            }
        });
    }


    public void getVoteStatisticsForRepresentative(String iid, final VoteStatisticsCallback callback) {

        String url = Uri.parse("http://data.riksdagen.se/voteringlista/")
                .buildUpon()
                .appendQueryParameter("iid", iid)
                .appendQueryParameter("utformat", "XML")
                .appendQueryParameter("gruppering", "namn")
                .build().toString();

        doCachedApiGetStringRequest(url, CacheRequest.CachingPolicy.MEDIUM_TIME_CACHE, new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new XmlToJson.Builder(response).build().toJson();
                    jsonObject = jsonObject.getJSONObject("voteringlista").getJSONObject("votering");
                    RepresentativeVoteStatistics stats = gson.fromJson(jsonObject.toString(), RepresentativeVoteStatistics.class);
                    callback.onStatisticsFetched(stats);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(new VolleyError("Failed to parse JSON"));
                }
            }

            @Override
            public void onFail(VolleyError error) {
                callback.onFail(error);
            }
            /*This stopped working when the API stopped serving JSON data for this URL. Might be worth keeping
              if it returns*/

            /*@Override
            public void onRequestSuccess(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("voteringlista").getJSONObject("votering");
                    RepresentativeVoteStatistics stats = gson.fromJson(jsonObject.toString(), RepresentativeVoteStatistics.class);
                    callback.onStatisticsFetched(stats);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onAudioSourceFail(new VolleyError("Failed to parse JSON"));
                }
            }

            @Override
            public void onRequestFail(VolleyError error) {
                callback.onAudioSourceFail(error);
            }*/
        });

    }

}
