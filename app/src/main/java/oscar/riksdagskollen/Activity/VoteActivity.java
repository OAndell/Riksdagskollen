package oscar.riksdagskollen.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.VoteResults;
import oscar.riksdagskollen.Util.JSONModel.PartyDocument;
import oscar.riksdagskollen.Util.JSONModel.Vote;
import oscar.riksdagskollen.Util.RiksdagenCallback.PartyDocumentCallback;
import oscar.riksdagskollen.Util.RiksdagenCallback.StringRequestCallback;

/**
 * Created by oscar on 2018-06-16.
 */

public class VoteActivity extends AppCompatActivity{

    private boolean graphLoaded = false;
    private boolean motionLoaded = false;
    private boolean motionHolderExpanded = false;
    private boolean partyVotesExpanded = false;

    private ViewGroup loadingView;
    private ScrollView mainContent;
    @ColorInt
    private int titleColor;
    private final String beslutStart = "<div id=\"step4\"";
    private LinearLayout motionHolder;
    private LinearLayout partyVotesHolder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(RiksdagskollenApp.getInstance().getThemeManager().getCurrentTheme(true));
        setContentView(R.layout.activity_vote);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBackgroundColor, this));
        }

        loadingView = findViewById(R.id.loading_view);
        mainContent = findViewById(R.id.main_content);

        ((ProgressBar) loadingView.findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(
                RiksdagskollenApp.getColorFromAttribute(R.attr.secondaryLightColor, this),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        titleColor = RiksdagskollenApp.getColorFromAttribute(R.attr.mainBodyTextColor, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.vote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Vote document = getIntent().getParcelableExtra("document");

        final TextView title = findViewById(R.id.vote_title);
        title.setText(document.getTitel());

        final RiksdagskollenApp app = RiksdagskollenApp.getInstance();
        if (document.getVoteResults() != null) {
            // Aldready downloaded results
            prepareGraphs(new VoteResults(document.getVoteResults()));
        } else {
            app.getRequestManager().getDownloadString("http:" + document.getDokument_url_html(), new StringRequestCallback() {
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




        final Context context = this;
        final TextView textBody = findViewById(R.id.point_title);
        final TextView result = findViewById(R.id.result_textview);
        final TextView proposition = findViewById(R.id.comitee_proposition);

        motionHolder = findViewById(R.id.motion_holder);
        partyVotesHolder = findViewById(R.id.party_votes_container);
        setUpCollapsibleViews();


        app.getRequestManager().getDownloadString(getBetUrl(document), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parseBodyFragment(response.substring(response.indexOf(beslutStart)));
                Integer pointNumber = Integer.valueOf(document.getTitel().split("förslagspunkt ")[1]);
                Element pointTitle = doc.select("#step4 > div > div > h4.medium:contains(" + pointNumber + ".)").first();
                String pointName = "Förslagspunkt " + pointNumber + ": " + pointTitle.text().substring(3).trim();
                Element resultSpan = pointTitle.nextElementSibling();
                Element propositionInfo = resultSpan.nextElementSibling();

                textBody.setText(pointName);
                result.setText(resultSpan.text().split("Beslut:")[1].trim());
                String propositionString;
                try {
                    propositionString = propositionInfo.text().split("förslag:")[1].trim();
                } catch (IndexOutOfBoundsException e) {
                    // Weird formatting
                    propositionString = "";
                }
                motionLoaded = true;

                Pattern motionPattern = Pattern.compile("[0-9]{4}\\/[0-9]{2}:[0-9]+");
                Matcher matcher = motionPattern.matcher(propositionInfo.text());
                System.out.println(propositionString);
                final ArrayList<String> motions = new ArrayList<>();
                int match = 0;
                int listID = 1;
                while (matcher.find()) {
                    String motionString = matcher.group(match);
                    motions.add(motionString);
                    propositionString = cleanupPropositionText(propositionString, motionString, listID);
                    listID++;
                }
                proposition.setText(boldKeywordsWithHTMl(propositionString));


                for (int i =0; i < motions.size(); i++) {
                    LayoutInflater layoutInflater = getLayoutInflater();
                    final TextView motionTitle = (TextView) layoutInflater.inflate(R.layout.vote_button_row, null);
                    final TextView lowerText = new TextView(context);
                    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 25);
                    ;
                    lowerText.setLayoutParams(params);
                    motionHolder.addView(motionTitle);
                    motionHolder.addView(lowerText);

                    final int finalI = i;
                    app.getRiksdagenAPIManager().getMotionByID(motions.get(i), new PartyDocumentCallback() {
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
                            motionTitle.setText("["+(finalI+1)+"] "+ motions.get(finalI) + " " + content);
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

                if (motions.isEmpty()) {
                    motionLoaded = true;
                    checkLoading();
                }
            }

            @Override
            public void onFail(VolleyError error) {

            }
        });

        /*

        app.getRequestManager().getDownloadString("http://data.riksdagen.se/dokument/" + document.getSearchableBetId() + ".html", new StringRequestCallback() {
                @Override
                public void onResponse(String response) {
                    try {
                        Document doc = Jsoup.parse(response);
                        Integer pointNumber = Integer.valueOf(document.getTitel().split("förslagspunkt ")[1]);
                        Elements pointTitle = doc.select("table:contains(" + pointNumber + ".)");
                        Element next = pointTitle.get(0);
                        String pointName = pointTitle.get(0).text().substring(3);

                        final HashMap<String, String> motions = new HashMap<>();
                        for (int i = 0; i < 10; i++) {
                            next = next.nextElementSibling();
                            if(next.text().contains(":") && next.text().contains("/") && next.text().indexOf(':') == 7){
                                Pattern propositionPattern = Pattern.compile("yrkande.* [0-9,–]*");
                                Matcher matcher = propositionPattern.matcher(next.text());
                                String prop = "";
                                if (matcher.find()){
                                    prop = matcher.group(0);
                                    prop = prop.replace("yrkandena", ": Förslag");
                                    prop = prop.replace("yrkande", ": Förslag");
                                }
                                motions.put(next.text().split(" ")[0], prop);
                            }
                            else if(next.toString().contains("Reservation")){
                                break;
                            }
                        }
                        textBody.setText(pointName);
                        for (final String key : motions.keySet()) {
                            app.getRiksdagenAPIManager().getMotionByID(key, new PartyDocumentCallback() {
                                @Override
                                public void onDocumentsFetched(List<PartyDocument> documents) {

                                    // Very uncommon case, for now we don't have a good method of comparing documents so skip it
                                    if (documents.size() > 1) {
                                        return;
                                    }

                                    final PartyDocument motionDocument = documents.get(0);


                                    LayoutInflater layoutInflater = getLayoutInflater();
                                    TextView motionTitle = (TextView) layoutInflater.inflate(R.layout.vote_button_row, null);
                                    SpannableString content = new SpannableString(motionDocument.getTitel());
                                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                    motionTitle.setText(content + motions.get(key));
                                    motionTitle.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(context, MotionActivity.class);
                                            intent.putExtra("document",((PartyDocument)motionDocument));
                                            startActivity(intent);
                                        }
                                    });
                                    motionHolder.addView(motionTitle);
                                    TextView lowerText = new TextView(context);
                                    lowerText.setText(motionDocument.getUndertitel()+ "\n");
                                    motionHolder.addView(lowerText);
                                    lowerText.setTextColor(titleColor);
                                    motionLoaded = true; //not true
                                    checkLoading();
                                }

                                @Override
                                public void onFail(VolleyError error) {

                                }
                            });
                        }
                    //We could not find the vote point for some reason
                    }catch (Exception e){
                        motionLoaded = true;
                        checkLoading();
                        textBody.setText("");
                    }
                }

                @Override
                public void onFail(VolleyError error) {

                }
            });
            */
    }

    private Spanned boldKeywordsWithHTMl(String input) {
        String[] keywords = {"avslår", "bifaller", "godkänner"};
        for (int i = 0; i < keywords.length; i++) {
            input = input.replaceAll(keywords[i], "<b>" + keywords[i] + "</b>");
        }
        return Html.fromHtml(input);
    }

    private String cleanupPropositionText(String text, String motionID, int listID) {
        //find yrkande
        Pattern pattern1 = Pattern.compile("(" + motionID + ").*?\\b(yrkandena)\\b.(\\d+(\\soch\\s|-)\\d+)");
        Matcher matcher1 = pattern1.matcher(text);

        Pattern pattern2 = Pattern.compile("(" + motionID + ").*?\\b(yrkande)\\b.(\\d+)");
        Matcher matcher2 = pattern2.matcher(text);

        Pattern pattern3 = Pattern.compile("(" + motionID + ")+(.*?)\\)");
        Matcher matcher3 = pattern3.matcher(text);

        if (matcher1.find()) {
            text = text.replace(matcher1.group(0), "[" + listID + "]");
            String yrkande = matcher1.group(3);
        } else if (matcher2.find()) {
            text = text.replace(matcher2.group(0), "[" + listID + "]");
            String yrkande = matcher2.group(3);
        } else if (matcher3.find()) {
            text = text.replace(matcher3.group(0), "[" + listID + "]");
            String yrkande = "-1";
        } else {
            text = text.replace(motionID, "[" + listID + "]");
            String yrkande = "-1";
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
        setupMainGraph(results);
        ArrayList<HorizontalBarChart> partyCharts = new ArrayList<>();
        //TODO fix a better solution if a party is not in parliament for a vote.
        //TODO this is hardcoded for votes before SD had any seats.
        if(results.getPartyVotes("SD")!=null){
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartS));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartM));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartSD));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartMP));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartC));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartV));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartL));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartKD));
            String[] parties = {"S", "M", "SD", "MP", "C", "V", "L", "KD"};
            setupPartyGraph(results, partyCharts, parties);
        }
        else {
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartS));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartM));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartMP));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartC));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartV));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartL));
            partyCharts.add((HorizontalBarChart) findViewById(R.id.chartKD));
            findViewById(R.id.chartSD).setVisibility(View.GONE);
            String[] parties = {"S", "M", "MP", "C", "V", "L", "KD"};
            setupPartyGraph(results, partyCharts, parties);
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

    private void setupMainGraph(VoteResults voteResults){
        BarData data = new BarData(getDataSet(voteResults.getTotal()));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        HorizontalBarChart chart =  findViewById(R.id.chart);
        chart.setData(data);
        chart.setDescription(null);

        //X-axis settings
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(2);
        xAxis.setAxisLineColor(titleColor);

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setTouchEnabled(false); //Remove ability to zoom n stuff

        //chart.setDrawBorders(true); //Looks nice to have this on, but values get outside the border
        chart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private BarDataSet getDataSet(int[] totalVotes){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.absentVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.refrainVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.noVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.yesVoteColor));

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1, totalVotes[3]));
        entries.add(new BarEntry(2, totalVotes[2]));
        entries.add(new BarEntry(3, totalVotes[1]));
        entries.add(new BarEntry(4, totalVotes[0]));;

        BarDataSet dataset = new BarDataSet(entries,"");
        dataset.setColors(colors);
        dataset.setDrawValues(true);
        return dataset;

    }

    private void setupPartyGraph(VoteResults voteResults, ArrayList<HorizontalBarChart> charts, String[] parties){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.yesVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.noVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.refrainVoteColor));
        colors.add(ContextCompat.getColor(this, R.color.absentVoteColor));

        for (int i = 0; i < charts.size(); i++) {
            HorizontalBarChart chart =  charts.get(i);

            ArrayList<BarEntry> yVals1 = new ArrayList<>();

            //TODO fix this horrible mess with old votes. Changes L -> FP
            if(parties[i].equals("L") && voteResults.getPartyVotes("L") == null){
                parties[i] = "FP";
            }
            int[] partyResults = voteResults.getPartyVotes(parties[i]);
            yVals1.add(new BarEntry(0, new float[]{partyResults[0], partyResults[1], partyResults[2], partyResults[3]}));


            BarDataSet set1 = new BarDataSet(yVals1, "");
            set1.setDrawIcons(false);
            set1.setColors(colors);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            //data.setValueTextColor(Color.BLACK);
            //TODO find a nice way of showing values, something with onclick seems best
            data.setDrawValues(false);

            chart.setData(data);

            chart.getXAxis().setDrawAxisLine(false);
            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setDrawLabels(false);

            chart.getAxisLeft().setDrawLabels(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisRight().setDrawLabels(false);
            chart.getAxisRight().setDrawGridLines(false);
            chart.getAxisRight().setDrawAxisLine(false);

            chart.getLegend().setEnabled(false);
            chart.setDrawValueAboveBar(false);
            chart.setFitBars(true);
            chart.setDescription(null);
            chart.setTouchEnabled(false); //Remove interactivity.
            chart.invalidate();
        }
    }


    private static void expand(final View v, Animation.AnimationListener listener) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }

    private static void collapse(final View v, Animation.AnimationListener listener) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(125);
        a.setAnimationListener(listener);
        v.startAnimation(a);
    }


}
