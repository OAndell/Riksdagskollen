package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Enum.CurrentParties;
import oscar.riksdagskollen.Util.Helper.VoteResults;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;
import oscar.riksdagskollen.Util.View.PartyVoteView;
import oscar.riksdagskollen.Util.View.VoteResultsView;

import static oscar.riksdagskollen.Util.Helper.AnimUtil.collapse;
import static oscar.riksdagskollen.Util.Helper.AnimUtil.expand;

/**
 * Created by oscar on 2018-06-16.
 */

public class VoteActivity extends AppCompatActivity{

    private boolean graphLoaded = false;
    private boolean motionLoaded = false;
    private boolean motionHolderExpanded = false;
    private boolean partyVotesExpanded = false;
    private Context context;
    private RiksdagskollenApp app;

    private ArrayList<MotionDetails> motions = new ArrayList<>();

    private ViewGroup loadingView;
    private ScrollView mainContent;
    @ColorInt
    private int titleColor;
    private final String parseStart = "<section class=\"component-case-content";
    private LinearLayout motionHolder;
    private LinearLayout partyVotesHolder;
    private LinearLayout voteResultChartHolder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_vote);
        context = this;
        app = RiksdagskollenApp.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        loadingView = findViewById(R.id.loading_view);
        mainContent = findViewById(R.id.main_content);
        motionHolder = findViewById(R.id.motion_holder);
        partyVotesHolder = findViewById(R.id.party_votes_container);
        voteResultChartHolder = findViewById(R.id.vote_activity_chart_holder);

        ((ProgressBar) loadingView.findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, this),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        titleColor = RiksdagskollenApp.getColorFromAttribute(R.attr.mainBodyTextColor, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.vote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Vote voteDocument = getIntent().getParcelableExtra("document");
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setCurrentScreen(this, "Vote doc: " + voteDocument.getId(), null);

        final TextView title = findViewById(R.id.vote_title);
        title.setText(voteDocument.getTitel());

        if (voteDocument.getVoteResults() != null) {
            // Aldready downloaded results
            prepareGraphs(new VoteResults(voteDocument.getVoteResults()));
        } else {
            app.getRequestManager().getDownloadString("http:" + voteDocument.getDokument_url_html(), new StringRequestCallback() {
                @Override
                public void onResponse(String response) {
                    VoteResults results = new VoteResults(response);
                    prepareGraphs(new VoteResults(results.getVoteResults()));
                }

                @Override
                public void onFail(VolleyError error) {

                }

            });
        }

        setUpCollapsibleViews();
        setUpTextAndGetMotions(voteDocument);
    }

    private void setUpTextAndGetMotions(final Vote voteDocument) {
        final TextView textBody = findViewById(R.id.point_title);
        final TextView result = findViewById(R.id.result_textview);
        final TextView proposition = findViewById(R.id.comitee_proposition);
        final TextView abstractTv = findViewById(R.id.vote_abstract);
        app.getRequestManager().getDownloadString(getBetUrl(voteDocument), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parseBodyFragment(response.substring(response.indexOf(parseStart)));

                Element abstractContainer = doc.select("section.component-case-content.component-case-section--plate > div.row.component-case-content > div").first();
                Elements abstractParagraphs = abstractContainer.select("p,li");

                StringBuilder abstractString = new StringBuilder();
                for (Element element : abstractParagraphs) {
                    if (element.is("li") && !element.text().contains("Teckenspråk") && !element.text().contains("Lättläst"))
                        abstractString.append("\t\t \u2022");
                    if (element.text().trim().length() > 0 && !element.text().contains("Teckenspråk") && !element.text().contains("Lättläst"))
                        abstractString.append(element.text()).append("\n\n");
                }
                abstractTv.setText(abstractString.toString());

                Integer pointNumber = Integer.valueOf(voteDocument.getTitel().split("förslagspunkt ")[1]);
                Element pointTitle = doc.select("#step4 > div > div > h4.medium:contains(" + pointNumber + ".)").first();
                String pointName = "Förslagspunkt " + pointNumber + ": " + pointTitle.text().substring(3).trim();
                Element resultSpan = pointTitle.nextElementSibling();
                Element propositionInfo = resultSpan.nextElementSibling();

                textBody.setText(pointName);
                try {
                    result.setText(resultSpan.text().split("Beslut:")[1].trim());
                } catch (IndexOutOfBoundsException e) {
                    //Beslut string does not exist yet. Do not show.
                    findViewById(R.id.vote_activity_beslut_label).setVisibility(View.GONE);
                    result.setVisibility(View.GONE);
                }
                String propositionString;
                try {
                    propositionString = propositionInfo.text().split("förslag:")[1].trim();
                } catch (IndexOutOfBoundsException e) {
                    // Weird formatting
                    propositionString = "";
                }
                motionLoaded = true;

                Pattern motionPattern = Pattern.compile("[0-9]{4}\\/[0-9]{2}:[A-ö]{0,4}[0-9]{0,4}");
                Matcher matcher = motionPattern.matcher(propositionInfo.text());
                final ArrayList<String> motionsIDs = new ArrayList<>();
                int match = 0;
                while (matcher.find()) {
                    String motionString = matcher.group(match);
                    motionsIDs.add(motionString);
                }
                propositionString = createMotionItemsAndCleanupPropositionText(propositionString, motionsIDs);
                proposition.setText(boldKeywordsWithHTMl(propositionString));
                displayMotions();

                if (motions.isEmpty()) {
                    motionLoaded = true;
                    checkLoading();
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    }

    //Loop through motions and display, fetch docs and make them clickable.
    private void displayMotions() {
        for (final MotionDetails motion : motions) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final TextView motionTitle = (TextView) layoutInflater.inflate(R.layout.vote_button_row, null);
            final TextView lowerText = new TextView(context);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 25);
            ;
            lowerText.setLayoutParams(params);
            motionHolder.addView(motionTitle);
            motionHolder.addView(lowerText);

            app.getRiksdagenAPIManager().getMotionByID(motion.id, new PartyDocumentCallback() {
                @Override
                public void onDocumentsFetched(List<PartyDocument> documents) {
                    // Very uncommon case, for now we don't have a good method of comparing documents so skip it
                    final PartyDocument motionDocument = documents.get(0);

                    if (motionDocument.getDoktyp().equals("prop")) {
                        motionHolder.removeView(lowerText);
                        motionTitle.setLayoutParams(params);
                    }
                    SpannableString content = new SpannableString(motionDocument.getTitel());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    motionTitle.setText("[" + (motion.getListPosition()) + "] " + motion.getId() + " " + content + " " + motion.getProposalPoint());
                    motionTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, MotionActivity.class);
                            intent.putExtra("document", ((PartyDocument) motionDocument));
                            startActivity(intent);
                        }
                    });
                    lowerText.setText(motionDocument.getUndertitel());
                    lowerText.setTextColor(titleColor);
                    motionLoaded = true; //not true
                    checkLoading();
                }

                @Override
                public void onFail(VolleyError error) {

                }
            });
        }
    }

    private Spanned boldKeywordsWithHTMl(String input) {
        String[] keywords = {"avslår", "bifaller", "godkänner"};
        for (int i = 0; i < keywords.length; i++) {
            input = input.replaceAll(keywords[i], "<b>" + keywords[i] + "</b>");
        }
        return Html.fromHtml(input);
    }

    //Create a motionDetail object for each document and replace it in the text with [x]
    private String createMotionItemsAndCleanupPropositionText(String text, ArrayList<String> motionIDs) {
        String originalText = text; //Save original to display if error occurs
        try {
            for (int i = 0; i < motionIDs.size(); i++) {
                int beginIndex = text.indexOf(motionIDs.get(i));
                int endIndex = text.length();
                if (motionIDs.size() > i + 1) {
                    endIndex = text.indexOf(motionIDs.get(i + 1));
                }
                //Cuts out string of interest ex "2017/18:3887 av Martin Kinnunen och Runar Filper (båda SD) yrkande 15 och "
                String relevantSubstring = text.substring(beginIndex, endIndex);
                if (relevantSubstring.contains("yrkande")) {
                    beginIndex = relevantSubstring.indexOf("yrkande");
                    //Cut out EX: "yrkande 15""
                    if (relevantSubstring.endsWith(", ")) {
                        endIndex = relevantSubstring.lastIndexOf(", ");
                    } else if (relevantSubstring.endsWith(" och ")) {
                        endIndex = relevantSubstring.lastIndexOf(" och ");
                    } else {
                        //Cut out the entire substring.
                        //This path often called when parsing the last motion.
                        endIndex = relevantSubstring.length() - 1;
                    }
                    String proposalPoint = relevantSubstring.substring(beginIndex, endIndex); //Get the "yrkande" points
                    motions.add(new MotionDetails(motionIDs.get(i), proposalPoint, i + 1));
                    text = text.replace(relevantSubstring.subSequence(0, endIndex), "[" + (i + 1) + "]");
                }
                //No "yrkanden"
                else {
                    //Replace until Ex".... (V)"
                    if (relevantSubstring.contains(")")) {
                        endIndex = relevantSubstring.lastIndexOf(")") + 1;
                        text = text.replace(relevantSubstring.subSequence(0, endIndex), "[" + (i + 1) + "]");
                    }
                    //Just replace the ID.
                    else {
                        text = text.replace(motionIDs.get(i), "[" + (i + 1) + "]");
                    }
                    motions.add(new MotionDetails(motionIDs.get(i), "", i + 1));
                }
            }
        } catch (Exception e) {
            //If all else fails
            motions.clear();
            for (int i = 0; i < motionIDs.size(); i++) {
                motions.add(new MotionDetails(motionIDs.get(i), "", i + 1));
            }
            return originalText;
        }
        return text;
    }

    private void setUpCollapsibleViews() {
        final ImageView expandMotionsHolder = findViewById(R.id.attended_documents_expand_icon);
        expandMotionsHolder.setRotation(180);
        findViewById(R.id.attended_documents_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (motionHolderExpanded) {
                    collapse(motionHolder, null);
                    motionHolderExpanded = false;
                    expandMotionsHolder.animate().rotation(180).setDuration(125).start();
                } else {
                    motionHolderExpanded = true;
                    expand(motionHolder, null);
                    expandMotionsHolder.animate().rotation(0).setDuration(125).start();
                }
            }
        });
        collapse(motionHolder, null);

        final ImageView expandPartyVotes = findViewById(R.id.party_votes_expand_icon);
        expandPartyVotes.setRotation(180);
        findViewById(R.id.party_votes_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (partyVotesExpanded) {
                    collapse(partyVotesHolder, null);
                    partyVotesExpanded = false;
                    expandPartyVotes.animate().rotation(180).setDuration(125).start();
                } else {
                    partyVotesExpanded = true;
                    expand(partyVotesHolder, null);
                    expandPartyVotes.animate().rotation(0).setDuration(125).start();
                }
            }
        });
        collapse(partyVotesHolder, null);

    }

    public static String getBetUrl(Vote document) {
        String baseURL = "http://riksdagen.se/sv/dokument-lagar/arende/betankande/";
        return baseURL + "_" + document.getSearchableBetId();
    }

    private void prepareGraphs(VoteResults results) {
        voteResultChartHolder.addView(new VoteResultsView(context, results));
        //TODO fix a better solution if a party is not in parliament for a vote.
        //TODO this is hardcoded for votes before SD had any seats.
        if(results.getPartyVotes("SD")!=null){
            String[] parties = {"S", "M", "SD", "C", "V", "KD", "L", "MP"};
            setupPartyGraph(results, parties);
        }
        else {
            String[] parties = {"S", "M", "C", "V", "KD", "L", "MP"};
            setupPartyGraph(results, parties);
        }
        graphLoaded = true;
        checkLoading();
    }


    private void checkLoading(){
        if(graphLoaded && motionLoaded){
            loadingView.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    private void setupPartyGraph(VoteResults voteResults, String[] parties) {
        for (int i = 0; i < parties.length; i++) {
            int partyLogo = CurrentParties.getParty(parties[i]).getDrawableLogo();
            //TODO fix this horrible mess with old votes. Changes L -> FP
            if(parties[i].equals("L") && voteResults.getPartyVotes("L") == null){
                parties[i] = "FP";
            }
            partyVotesHolder.addView(new PartyVoteView(context, partyLogo, voteResults.getPartyVotes(parties[i])));
        }
    }


    private class MotionDetails {
        private String id;
        private String proposalPoint;
        private int listPosition;

        public MotionDetails(String id, String proposalPoint, int listPosition) {
            this.id = id;
            this.proposalPoint = proposalPoint;
            this.listPosition = listPosition;
        }

        public String getId() {
            return id;
        }

        public String getProposalPoint() {
            return proposalPoint;
        }

        public int getListPosition() {
            return listPosition;
        }
    }
}
